package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;

public class HandlerClass {

    private UserService userService;
    private GameService gameService;
    private ClearService clearService;
    private Gson serializer;

    public HandlerClass(){
        userService = new UserService();
        gameService = new GameService();
        clearService = new ClearService();
        serializer = new Gson();
    }

    public String registerHandler(String json) throws DataAccessException {
        RegisterRequest request = serializer.fromJson(json, RegisterRequest.class);
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        RegisterResult result = userService.register(request);
        return serializer.toJson(result);
    }

    public String loginHandler(String json) throws DataAccessException {
        LoginRequest request = serializer.fromJson(json, LoginRequest.class);
        LoginResult result = userService.login(request);
        return serializer.toJson(result);
    }

    public String logoutHandler(String json) throws DataAccessException {
        LogoutRequest request = new LogoutRequest(json);
        LogoutResult result = userService.logout(request);
        return serializer.toJson(result);
    }

    public String listGamesHandler(String json) throws DataAccessException {
        ListRequest request = new ListRequest(json);
        ListResult result = gameService.listGames(request);
        return serializer.toJson(result);
    }

    public String createGameHandler(String json, String auth) throws DataAccessException {
        GameRequest gameName = serializer.fromJson(json, GameRequest.class);
        GameRequest request = new GameRequest(gameName.gameName(), auth);
        if (request.gameName() == null || request.authToken() == null) {
            throw new DataAccessException("Error: bad request");
        }
        GameResult result = gameService.createGame(request);
        return serializer.toJson(result);
    }

    public String joinGameHandler(String json, String auth) throws DataAccessException {
        JoinRequest colorAndID = serializer.fromJson(json, JoinRequest.class);
        JoinRequest request = new JoinRequest(colorAndID.playerColor(), colorAndID.gameID(), auth);
        if (request.playerColor() == null || request.authToken() == null || request.gameID() == 0) {
            throw new DataAccessException("Error: bad request");
        }
        JoinResult result = gameService.joinGame(request);
        return serializer.toJson(result);
    }

    public String clearHandler() {
        ClearResult result = clearService.delete();
        return serializer.toJson(result);
    }
}
