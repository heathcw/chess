package ui;

import java.util.*;

import chess.*;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.*;

public class ChessClient {

    private String user = null;
    private String authToken = null;
    private String team = null;
    private int gameId;
    private String chessString = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private final Collection<String[]> games = new ArrayList<>();
    private final Map<Integer, Integer> idMap = new HashMap<>();
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private final String url;

    public ChessClient(String url, NotificationHandler notificationHandler) {
        server = new ServerFacade(url);
        this.notificationHandler = notificationHandler;
        this.url = url;
    }

    public String eval(String input) {
        ChessGame game = new ChessGame();
        var serializer = new Gson();
        String json = serializer.toJson(game);
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                case "debug" -> loadWhiteGame(json);
                case "black" -> loadBlackGame(json);
                case "move" -> makeMove(params);
                case "redraw" -> redraw(chessString);
                default -> help();
            };
        } catch (ResponseException ex) {
            return SET_TEXT_COLOR_RED + ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length == 3) {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = server.register(request);
            state = State.SIGNEDIN;
            user = result.username();
            authToken = result.authToken();
            return String.format("You logged in as %s.", user);
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        assertSignedOut();
        if (params.length == 2) {
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = server.login(request);
            state = State.SIGNEDIN;
            user = result.username();
            authToken = result.authToken();
            return String.format("You logged in as %s.", user);
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String create(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            GameRequest request = new GameRequest(params[0], authToken);
            server.createGame(request);
            return String.format("You created game: %s.", request.gameName());
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String list() throws ResponseException {
        assertSignedIn();
        AuthRequest request = new AuthRequest(authToken);
        ListResult result = server.listGames(request);
        int number = 1;
        games.clear();
        idMap.clear();
        for (GameData game: result.games()) {
            String white = game.whiteUsername();
            String black = game.blackUsername();
            if (white == null) {
                white = SET_TEXT_COLOR_WHITE + "JOIN" + SET_TEXT_COLOR_BLUE;
            }
            if (black == null) {
                black = SET_TEXT_COLOR_BLACK + "JOIN" + SET_TEXT_COLOR_BLUE;
            }
            String[] add = {Integer.toString(number), game.gameName(), white, black};
            games.add(add);
            idMap.put(number, game.gameID());
            number += 1;
        }
        var list = new StringBuilder();
        list.append("GameID, GameName, Player:White, Player:Black").append('\n');
        for (String[] game : games) {
            list.append(Arrays.toString(game)).append('\n');
        }
        return list.toString();
    }

    public String join(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2 && params[0].matches("[0-9]+")) {
            int number = Integer.parseInt(params[0]);
            if (number > idMap.size() || number < 1) {
                throw new ResponseException(400, "Cannot find game. List games again.");
            }
            int id = idMap.get(number);
            JoinRequest request = new JoinRequest(params[1].toUpperCase(), id, authToken);
            server.joinGame(request);
            UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, id);
            ws = new WebSocketFacade(url, notificationHandler);
            ws.connect(connect);
            state = State.INGAME;
            gameId = id;
            if (request.playerColor().equals("BLACK")) {
                team = "Black";
                return "Joined game as black";
            }
            team = "White";
            return "Joined game as white";
        }
        throw new ResponseException(400, "Expected: <ID> <WHITE|BLACK>");
    }

    public String observe(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1 && params[0].matches("[0-9]+")) {
            int number = Integer.parseInt(params[0]);
            if (number > idMap.size() || number < 1) {
                throw new ResponseException(400, "Cannot find game. List games again.");
            }
            int id = idMap.get(number);
            System.out.printf("observing game: %s%n", number);
            state = State.INGAME;
            team = "White";
            gameId = id;
            UserGameCommand connect = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, id);
            ws = new WebSocketFacade(url, notificationHandler);
            ws.connect(connect);
            return "Joined game as an observer";
        }
        throw new ResponseException(400, "Expected: <ID>");
    }

    public String logout() throws ResponseException {
        assertSignedIn();
        AuthRequest request = new AuthRequest(authToken);
        server.logout(request);
        state = State.SIGNEDOUT;
        return "You logged out";
    }

    public String redraw(String game) {
        if (state != State.INGAME || game == null) {
            return "";
        }
        chessString = game;
        if (team.equals("White")) {
            return loadWhiteGame(game);
        } else {
            return loadBlackGame(game);
        }
    }

    public String makeMove(String... params) throws ResponseException {
        assertInGame();
        Map<String, Integer> columns = new HashMap<>();
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        int i = 1;
        for (String letter : letters) {
            columns.put(letter, i);
            i++;
        }
        if (params.length == 2) {
            int row = params[0].charAt(0) - '0';
            int col = columns.get(Character.toString(params[0].charAt(1)));
            ChessPosition start = new ChessPosition(row, col);
            row = params[1].charAt(0) - '0';
            col = columns.get(Character.toString(params[1].charAt(1)));
            ChessPosition end = new ChessPosition(row, col);
            ChessMove move = new ChessMove(start, end, null);
            MakeMoveCommand command = new MakeMoveCommand(authToken, gameId, move);
            ws.makeMove(command);
            return "";
        }
        throw new ResponseException(400, "Expected: <STARTPOSITION> <ENDPOSITION>");
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - quit
                    - help
                    """;
        } else if (state == State.INGAME) {
            return """
                    - redraw
                    - move <STARTPOSITION> <ENDPOSITION>
                    - resign
                    - leave
                    - help
                    """;
        }
        return """
                - create <NAME>
                - list
                - join <ID> <WHITE|BLACK>
                - observe <ID>
                - logout
                - quit
                - help
                """;
    }

    private String loadWhiteGame(String chessString) {
        var serializer = new Gson();
        ChessGame game = serializer.fromJson(chessString, ChessGame.class);
        StringBuilder board = new StringBuilder();
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        boolean white;

        //top letters
        board.append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_TEXT_BOLD).append(SET_TEXT_COLOR_BLACK);
        for (String letter: letters) {
            board.append(" ").append(letter).append('\u2003');
        }
        board.append("   ").append(RESET_BG_COLOR).append('\n');

        //rows
        for (int i = 8; i > 0; i--) {
            white = i % 2 == 0;
            board.append(boardRow(i, game.getBoard().getBoard(), white));
        }

        //bottom letters
        board.append(SET_BG_COLOR_LIGHT_GREY).append("   ");
        for (String letter: letters) {
            board.append(" ").append(letter).append('\u2003');
        }
        board.append("   ").append(RESET_BG_COLOR).append('\n');

        return board.toString();
    }

    private String loadBlackGame(String chessString) {
        var serializer = new Gson();
        ChessGame game = serializer.fromJson(chessString, ChessGame.class);
        StringBuilder board = new StringBuilder();
        String[] letters = {"h", "g", "f", "e", "d", "c", "b", "a"};
        boolean white;

        //top letters
        board.append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_TEXT_BOLD).append(SET_TEXT_COLOR_BLACK);
        for (String letter: letters) {
            board.append(" ").append(letter).append('\u2003');
        }
        board.append("   ").append(RESET_BG_COLOR).append('\n');

        //rows
        for (int i = 1; i <= 8; i++) {
            white = i % 2 == 0;
            board.append(boardRow(i, game.getBoard().getBoard(), white));
        }

        //bottom letters
        board.append(SET_BG_COLOR_LIGHT_GREY).append("   ");
        for (String letter: letters) {
            board.append(" ").append(letter).append('\u2003');
        }
        board.append("   ").append(RESET_BG_COLOR).append('\n');

        return board.toString();
    }


    private String boardRow(int rowNumber, ChessPiece[][] board, boolean white) {
        String firstNumber = Integer.toString(rowNumber);
        rowNumber--;
        StringBuilder row = new StringBuilder();
        row.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(firstNumber).append(" ");
        for (int i = 0; i < 8; i++) {
            if (white) {
                row.append(SET_BG_COLOR_WHITE);
                white = false;
            } else {
                row.append(SET_BG_COLOR_RED);
                white = true;
            }
            if (board[rowNumber][i] == null) {
                row.append(EMPTY);
                continue;
            }
            switch (board[rowNumber][i].getPieceType()) {
                case ChessPiece.PieceType.PAWN -> {
                    switch (board[rowNumber][i].getTeamColor()) {
                        case WHITE -> row.append(WHITE_PAWN);
                        case BLACK -> row.append(BLACK_PAWN);
                    }
                }
                case ChessPiece.PieceType.BISHOP -> {
                    switch (board[rowNumber][i].getTeamColor()) {
                        case WHITE -> row.append(WHITE_BISHOP);
                        case BLACK -> row.append(BLACK_BISHOP);
                    }
                }
                case ChessPiece.PieceType.ROOK -> {
                    switch (board[rowNumber][i].getTeamColor()) {
                        case WHITE -> row.append(WHITE_ROOK);
                        case BLACK -> row.append(BLACK_ROOK);
                    }
                }
                case ChessPiece.PieceType.KNIGHT -> {
                    switch (board[rowNumber][i].getTeamColor()) {
                        case WHITE -> row.append(WHITE_KNIGHT);
                        case BLACK -> row.append(BLACK_KNIGHT);
                    }
                }
                case ChessPiece.PieceType.KING -> {
                    switch (board[rowNumber][i].getTeamColor()) {
                        case WHITE -> row.append(WHITE_KING);
                        case BLACK -> row.append(BLACK_KING);
                    }
                }
                case ChessPiece.PieceType.QUEEN -> {
                    switch (board[rowNumber][i].getTeamColor()) {
                        case WHITE -> row.append(WHITE_QUEEN);
                        case BLACK -> row.append(BLACK_QUEEN);
                    }
                }
            }
        }
        row.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append(firstNumber).append(" ").append(RESET_BG_COLOR);
        row.append('\n');
        return row.toString();
    }

    private void assertSignedIn() throws ResponseException {
        if (state != State.SIGNEDIN) {
            throw new ResponseException(400, "You must log in");
        }
    }

    private void assertSignedOut() throws ResponseException {
        if (state != State.SIGNEDOUT) {
            throw new ResponseException(400, "You are already logged in");
        }
    }

    private void assertInGame() throws ResponseException {
        if (state != State.INGAME) {
            throw new ResponseException(400, "You must join a game");
        }
    }
}
