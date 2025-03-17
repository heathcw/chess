package ui;

import java.util.Arrays;

import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import service.*;

public class ChessClient {

    private String user = null;
    private String authToken = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public ChessClient(String url) {
        server = new ServerFacade(url);
        this.serverUrl = url;
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
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            state = State.SIGNEDIN;
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = server.register(request);
            user = result.username();
            authToken = result.authToken();
            return String.format("You signed in as %s.", user);
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            state = State.SIGNEDIN;
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = server.login(request);
            user = result.username();
            authToken = result.authToken();
            return String.format("You signed in as %s.", user);
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
        var list = new StringBuilder();
        var gson = new Gson();
        for (GameData game : result.games()) {
            list.append(gson.toJson(game)).append('\n');
        }
        return list.toString();
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
            throw new ResponseException(400, "You must sign in");
        }
    }
}
