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
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    Gson serializer = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) {
        try {
            UserGameCommand command = serializer.fromJson(msg, UserGameCommand.class);

            String user = getUser(command.getAuthToken());
            saveSession(command.getGameID(), session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, user, command);
                case MAKE_MOVE -> makeMove(session, user, (MakeMoveCommand) command);
                case LEAVE -> leave(session, user, command);
                case RESIGN -> resign(session, user, command);
            }
        } catch (DataAccessException | IOException | InvalidMoveException e) {
            sendMessage();
        }
    }

    private String getUser(String authToken) throws DataAccessException {
        SQLAuthDAO auths = new SQLAuthDAO();
        AuthData user = auths.getAuth(authToken);
        return user.username();
    }

    private void saveSession(int gameID, Session session) {}

    private void sendMessage() {}

    private void connect(Session session, String user, UserGameCommand command) throws IOException {
        connections.add(user, session);
        var message = String.format("%s connected", user);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(user, notification);
    }

    private void makeMove(Session session, String user, MakeMoveCommand command) throws DataAccessException, InvalidMoveException, IOException {
        SQLGameDAO games = new SQLGameDAO();
        games.updateGame(command.getMove(), command.getGameID());
        ChessGame game = games.getGameByID(command.getGameID()).game();
        String json = serializer.toJson(game);
        var message = String.format("%s made a move", user);
        NotificationMessage notification = new NotificationMessage(message);
        connections.broadcast(user, notification);
        LoadGameMessage load = new LoadGameMessage(json);
        connections.load(load);
    }

    private void leave(Session session, String user, UserGameCommand command) throws IOException {
        connections.remove(user);
        var message = String.format("%s left the game", user);
        var notification = new NotificationMessage(message);
        connections.broadcast(user, notification);
    }

    private void resign(Session session, String user, UserGameCommand command) {}
}
