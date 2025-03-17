package ui;

import exception.ResponseException;
import service.LoginRequest;
import service.LoginResult;
import service.RegisterRequest;
import service.RegisterResult;

public class ChessClient {

    private String user = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public ChessClient(String url) {
        server = new ServerFacade(url);
        this.serverUrl = url;
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            state = State.SIGNEDIN;
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = server.register(request);
            user = result.username();
            return String.format("You signed in as %s.", user);
        }
        throw new ResponseException(400, "Expected: <username> <password> <email>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            state = State.SIGNEDIN;
            LoginRequest request = new LoginRequest(params[0], params[1]);
            LoginResult result = server.login(request);
            user = result.username();
            return String.format("You signed in as %s.", user);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
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
