package ui;

import java.util.*;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import websocket.commands.UserGameCommand;

import static ui.EscapeSequences.*;

public class ChessClient {

    private String user = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private final Collection<String[]> games = new ArrayList<>();
    private final Map<Integer, Integer> idMap = new HashMap<>();
    private NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private String url;

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
                case "black" -> createBlackBoard();
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
            if (request.playerColor().equals("BLACK")) {
                return createBlackBoard();
            }
            return createWhiteBoard();
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
            return createWhiteBoard();
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

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <USERNAME> <PASSWORD> <EMAIL>
                    - login <USERNAME> <PASSWORD>
                    - quit
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

    private String createWhiteBoard() {
        boolean white = true;
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] blackPieces = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP,
                BLACK_KNIGHT, BLACK_ROOK};
        String[] whitePieces = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP,
                WHITE_KNIGHT, WHITE_ROOK};
        StringBuilder board = new StringBuilder();
        board.append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_TEXT_BOLD).append(SET_TEXT_COLOR_BLACK);
        for (String letter: letters) {
            board.append(" ").append(letter).append('\u2003');
        }
        board.append("   ").append(RESET_BG_COLOR).append('\n');

        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("8").append(" ");
        for (String piece: blackPieces) {
            if (white) {
                board.append(SET_BG_COLOR_WHITE);
                white = false;
            } else {
                board.append(SET_BG_COLOR_RED);
                white = true;
            }
            board.append(piece);
        }
        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("8").append(" ").append(RESET_BG_COLOR).append('\n');

        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("7").append(" ");
        board.append((SET_BG_COLOR_RED + BLACK_PAWN + SET_BG_COLOR_WHITE + BLACK_PAWN).repeat(4));
        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("7").append(" ").append(RESET_BG_COLOR).append('\n');

        board.append(boardRow(6, null, white));
        board.append(boardRow(4, null, white));

        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("2").append(" ");
        board.append((SET_BG_COLOR_WHITE + WHITE_PAWN + SET_BG_COLOR_RED + WHITE_PAWN).repeat(4));
        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("2").append(" ").append(RESET_BG_COLOR).append('\n');

        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("1").append(" ");
        white = false;
        for (String piece: whitePieces) {
            if (white) {
                board.append(SET_BG_COLOR_WHITE);
                white = false;
            } else {
                board.append(SET_BG_COLOR_RED);
                white = true;
            }
            board.append(piece);
        }
        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("1").append(" ").append(RESET_BG_COLOR).append('\n');

        board.append(SET_BG_COLOR_LIGHT_GREY).append("   ");
        for (String letter: letters) {
            board.append(" ").append(letter).append('\u2003');
        }
        board.append("   ").append(RESET_BG_COLOR).append('\n');

        return board.toString();
    }

    private String loadWhiteGame(String chessString) {
        var serializer = new Gson();
        ChessGame game = serializer.fromJson(chessString, ChessGame.class);
        StringBuilder board = new StringBuilder();
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        boolean white = true;

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

    private String createBlackBoard() {
        boolean white = true;
        String[] letters = {"h", "g", "f", "e", "d", "c", "b", "a"};
        String[] blackPieces = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP,
                BLACK_KNIGHT, BLACK_ROOK};
        String[] whitePieces = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP,
                WHITE_KNIGHT, WHITE_ROOK};
        StringBuilder board = new StringBuilder();
        board.append(SET_BG_COLOR_LIGHT_GREY).append("   ").append(SET_TEXT_BOLD).append(SET_TEXT_COLOR_BLACK);
        for (String letter: letters) {
            board.append(" ").append(letter).append('\u2003');
        }
        board.append("   ").append(RESET_BG_COLOR).append('\n');

        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("1").append(" ");
        for (String piece: whitePieces) {
            if (white) {
                board.append(SET_BG_COLOR_WHITE);
                white = false;
            } else {
                board.append(SET_BG_COLOR_RED);
                white = true;
            }
            board.append(piece);
        }
        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("1").append(" ").append(RESET_BG_COLOR).append('\n');

        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("2").append(" ");
        board.append((SET_BG_COLOR_RED + WHITE_PAWN + SET_BG_COLOR_WHITE + WHITE_PAWN).repeat(4));
        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("2").append(" ").append(RESET_BG_COLOR).append('\n');

        board.append(boardRow(3, null, white));
        board.append(boardRow(5, null, white));

        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("7").append(" ");
        board.append((SET_BG_COLOR_WHITE + BLACK_PAWN + SET_BG_COLOR_RED + BLACK_PAWN).repeat(4));
        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("7").append(" ").append(RESET_BG_COLOR).append('\n');

        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("8").append(" ");
        white = false;
        for (String piece: blackPieces) {
            if (white) {
                board.append(SET_BG_COLOR_WHITE);
                white = false;
            } else {
                board.append(SET_BG_COLOR_RED);
                white = true;
            }
            board.append(piece);
        }
        board.append(SET_BG_COLOR_LIGHT_GREY).append(" ").append("8").append(" ").append(RESET_BG_COLOR).append('\n');

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
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must log in");
        }
    }

    private void assertSignedOut() throws ResponseException {
        if (state == State.SIGNEDIN) {
            throw new ResponseException(400, "You are already logged in");
        }
    }
}
