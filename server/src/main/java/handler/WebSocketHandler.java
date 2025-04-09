package handler;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    Gson serializer = new Gson();
    SQLGameDAO games;

    {
        try {
            games = new SQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) {
        String user;
        try {
            UserGameCommand command = serializer.fromJson(msg, UserGameCommand.class);

            user = getUser(command.getAuthToken());
            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, user, command);
                case MAKE_MOVE -> {
                        MakeMoveCommand move = serializer.fromJson(msg, MakeMoveCommand.class);
                        makeMove(session, user, move);
                }
                case LEAVE -> leave(session, user, command);
                case RESIGN -> resign(session, user, command);
            }
        } catch (DataAccessException | IOException | InvalidMoveException e) {
            sendMessage(session, e);
        }
    }

    private String getUser(String authToken) throws DataAccessException {
        SQLAuthDAO auths = new SQLAuthDAO();
        AuthData user = auths.getAuth(authToken);
        if (user == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        return user.username();
    }

    private String getTeam(String user, UserGameCommand command) {
        GameData data = games.getGameByID(command.getGameID());
        if (data.whiteUsername()!= null && data.whiteUsername().equals(user)) {
            return "White";
        } else if (data.blackUsername() != null && data.blackUsername().equals(user)) {
            return "Black";
        } else {
            return "Observer";
        }
    }

    private String convertMoveToString(MakeMoveCommand command) {
        Map<Integer, String> columns = new HashMap<>();
        String[] whiteLetters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        int i = 1;
        for (String letter : whiteLetters) {
            columns.put(i, letter);
            i++;
        }
        String start = String.format("%s%s", columns.get(command.getMove().getStartPosition().getColumn() + 1),
                command.getMove().getStartPosition().getRow() + 1);
        String end = String.format("%s%s", columns.get(command.getMove().getEndPosition().getColumn() + 1),
                command.getMove().getEndPosition().getRow() + 1);
        return start + " to " + end;
    }

    private void saveSession(int gameID, Session session) {}

    private void sendMessage(Session session, Exception e) {
        ErrorMessage error = new ErrorMessage(e.getMessage());
        try {
            connections.error(session, error);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void connect(Session session, String user, UserGameCommand command) throws IOException, DataAccessException {
        if (games.getGameByID(command.getGameID()) == null) {
            throw new DataAccessException("Error: game does not exist");
        }
        connections.add(command.getGameID(), user, session);
        String team = getTeam(user, command);
        var message = String.format("%s joined as %s", user, team);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(command.getGameID(), user, notification);
        ChessGame game = games.getGameByID(command.getGameID()).game();
        String json = serializer.toJson(game);
        connections.load(command.getGameID(), new LoadGameMessage(json), user);
    }

    private void makeMove(Session session, String user, MakeMoveCommand command) throws DataAccessException, InvalidMoveException, IOException {
        GameData data = games.getGameByID(command.getGameID());
        if (data.game().getTeamTurn() == ChessGame.TeamColor.WHITE) {
            if (data.whiteUsername() == null || !data.whiteUsername().equals(user)) {
                throw new InvalidMoveException("Error: not your turn");
            }
        } else if (data.blackUsername() == null || !data.blackUsername().equals(user)) {
            throw new InvalidMoveException("Error: not your turn");
        }
        games.updateGame(command.getMove(), command.getGameID());
        ChessGame game = games.getGameByID(command.getGameID()).game();
        String json = serializer.toJson(game);
        var message = String.format("%s moved " + convertMoveToString(command), user);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(command.getGameID(), user, notification);
        LoadGameMessage load = new LoadGameMessage(json);
        connections.load(command.getGameID(), load, user);
        connections.broadcast(command.getGameID(), user, load);
        if (game.isInStalemate(ChessGame.TeamColor.WHITE) || game.isInStalemate(ChessGame.TeamColor.BLACK)) {
            message = "Stalemate!";
            notification = new NotificationMessage(message);
            connections.broadcast(command.getGameID(), user, notification);
            connections.notification(command.getGameID(), notification, user);
        }
        else if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            game.gameOver();
            if (data.whiteUsername().equals(user)) {
                message = "Checkmate, You lose!";
                notification = new NotificationMessage(message);
                connections.notification(command.getGameID(), notification, user);
                message = "Checkmate, White lost!";
            } else {
                message = "Checkmate, You win!";
                notification = new NotificationMessage(message);
                connections.notification(command.getGameID(), notification, user);
                message = "Checkmate, White lost!";
            }
            notification = new NotificationMessage(message);
            connections.broadcast(command.getGameID(), user, notification);
        } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            game.gameOver();
            if (data.blackUsername().equals(user)) {
                message = "Checkmate, You lose!";
                notification = new NotificationMessage(message);
                connections.notification(command.getGameID(), notification, user);
                message = "Checkmate, Black lost!";
            } else {
                message = "Checkmate, You win!";
                notification = new NotificationMessage(message);
                connections.notification(command.getGameID(), notification, user);
                message = "Checkmate, Black lost!";
            }
            notification = new NotificationMessage(message);
            connections.broadcast(command.getGameID(), user, notification);
        }
         else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            message = "White is in check";
            notification = new NotificationMessage(message);
            connections.broadcast(command.getGameID(), user, notification);
            connections.notification(command.getGameID(), notification, user);
        }
         else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            message = "Black is in check";
            notification = new NotificationMessage(message);
            connections.broadcast(command.getGameID(), user, notification);
            connections.notification(command.getGameID(), notification, user);
        }
    }

    private void leave(Session session, String user, UserGameCommand command) throws IOException, DataAccessException {
        GameData data = games.getGameByID(command.getGameID());
        if (data.whiteUsername() != null && data.whiteUsername().equals(user)) {
            games.leaveGame("WHITE", command.getGameID());
        } else if (data.blackUsername() != null && data.blackUsername().equals(user)) {
            games.leaveGame("BLACK", command.getGameID());
        }
        var message = String.format("%s left the game", user);
        var notification = new NotificationMessage(message);
        connections.broadcast(command.getGameID(), user, notification);
        connections.remove(command.getGameID(), user);
    }

    private void resign(Session session, String user, UserGameCommand command) throws DataAccessException, IOException, InvalidMoveException {
        GameData data = games.getGameByID(command.getGameID());
        if ((data.blackUsername() == null || !data.blackUsername().equals(user)) && (data.whiteUsername() == null
                || !data.whiteUsername().equals(user))) {
            throw new InvalidMoveException("Error: you are an observer");
        }
        games.gameOver(command.getGameID());
        var message = String.format("%s resigned", user);
        var notification = new NotificationMessage(message);
        connections.broadcast(command.getGameID(), user, notification);
        message = "you resigned";
        notification = new NotificationMessage(message);
        connections.notification(command.getGameID(), notification, user);
    }
}
