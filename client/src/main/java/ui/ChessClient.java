package ui;

import java.util.*;

import exception.ResponseException;
import model.*;

import static ui.EscapeSequences.*;

public class ChessClient {

    private String user = null;
    private String authToken = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private final Collection<String[]> games = new ArrayList<>();
    private final Map<Integer, Integer> idMap = new HashMap<>();

    public ChessClient(String url) {
        server = new ServerFacade(url);
    }

    public String eval(String input) {
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
                case "logout" -> logout();
                case "debug" -> createBoard();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
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
            String[] add = {Integer.toString(number), game.gameName(), game.whiteUsername(), game.blackUsername()};
            games.add(add);
            idMap.put(number, game.gameID());
            number += 1;
        }
        var list = new StringBuilder();
        for (String[] game : games) {
            list.append(Arrays.toString(game)).append('\n');
        }
        return list.toString();
    }

    public String join(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            int number = Integer.parseInt(params[0]);
            int id = idMap.get(number);
            JoinRequest request = new JoinRequest(params[1].toUpperCase(), id, authToken);
            server.joinGame(request);
            if (request.playerColor().equals("BLACK")) {
                return new StringBuilder(createBoard()).reverse().toString();
            }
            return createBoard();
        }
        throw new ResponseException(400, "Expected: <ID> <WHITE|BLACK>");
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

    private String createBoard() {
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

        board.append(boardRow(6));
        board.append(boardRow(4));

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

    private String boardRow(int rowNumber) {
        String firstNumber = Integer.toString(rowNumber);
        String secondNumber = Integer.toString(rowNumber - 1);
        String emptyRow = (SET_BG_COLOR_WHITE + EMPTY + SET_BG_COLOR_RED + EMPTY).repeat(4);
        String reverseRow = (SET_BG_COLOR_RED + EMPTY + SET_BG_COLOR_WHITE + EMPTY).repeat(4);
        return SET_BG_COLOR_LIGHT_GREY + " " + firstNumber + " " + emptyRow + SET_BG_COLOR_LIGHT_GREY + " "
                + firstNumber + " " + RESET_BG_COLOR + '\n' + SET_BG_COLOR_LIGHT_GREY + " " + secondNumber + " "
                + reverseRow + SET_BG_COLOR_LIGHT_GREY + " " + secondNumber + " " + RESET_BG_COLOR + '\n';
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
