package ui;

import exception.ResponseException;
import com.google.gson.Gson;
import model.AuthData;
import service.*;

import java.net.*;
import java.io.*;
import java.util.Objects;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest request) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, request, RegisterResult.class);
    }

    public LoginResult login(LoginRequest request) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, request, LoginResult.class);
    }

    public LogoutResult logout(AuthRequest request) throws ResponseException {
        var path = "/session";
        return this.makeRequest("DELETE", path, request, LogoutResult.class);
    }

    public ListResult listGames(AuthRequest request) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, request, ListResult.class);
    }

    public GameResult createGame(GameRequest request) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, request, GameResult.class);
    }

    public JoinResult joinGame(JoinRequest request) throws ResponseException {
        var path = "/game";
        return this.makeRequest("PUT", path, request, JoinResult.class);
    }

    public void clear() throws ResponseException {
        var path = "/db";
        makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if ((method.equals("DELETE") && path.equals("/session")) || (method.equals("GET") && path.equals("/game"))) {
                writeHeader(request, http);
            } else {
                writeBody(request, http);
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private static void writeHeader(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            if (request.getClass() == AuthRequest.class) {
                http.setRequestProperty("Authorization", ((AuthRequest) request).authToken());
            }
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
