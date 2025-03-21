package ui;

import java.util.*;

import exception.ResponseException;
import model.GameData;
import service.*;

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
            return String.format("You joined game: %s", number);
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
