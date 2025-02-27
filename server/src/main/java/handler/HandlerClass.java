package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.*;

public class HandlerClass {

    private UserService userService;
    private GameService gameService;
    private Gson serializer;

    public HandlerClass(){
        userService = new UserService();
        gameService = new GameService();
        serializer = new Gson();
    }

    public String registerHandler(String json) {
        RegisterRequest request = serializer.fromJson(json, RegisterRequest.class);
        RegisterResult result = userService.register(request);
        return serializer.toJson(result);
    }

    public String loginHandler(String json) {
        LoginRequest request = serializer.fromJson(json, LoginRequest.class);
        LoginResult result = userService.login(request);
        return serializer.toJson(result);
    }

    public String logoutHandler(String json) throws DataAccessException {
        LogoutRequest request = serializer.fromJson(json, LogoutRequest.class);
        LogoutResult result = userService.logout(request);
        return serializer.toJson(result);
    }
}
