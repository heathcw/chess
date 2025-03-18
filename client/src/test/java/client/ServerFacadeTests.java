package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import service.*;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(1080);
        System.out.println("Started test HTTP server on " + port);
        var serverUrl = "http://localhost:1080";
        facade = new ServerFacade(serverUrl);
        try {
            facade.clear();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerTest() throws ResponseException {
        RegisterRequest request = new RegisterRequest("user", "pass", "em");
        RegisterResult result = facade.register(request);
        assert result.username().equals("user");
    }

    @Test
    public void failedRegisterTest() {
        RegisterRequest request = new RegisterRequest("user", "empty", null);
        try {
            facade.register(request);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            assert e.getMessage().equals("Cannot invoke \"java.lang.Double.intValue()\" because the return value of \"java.util.HashMap.get(Object)\" is null");
        }
    }

    @Test
    public void loginTest() throws ResponseException {
        RegisterRequest regRequest = new RegisterRequest("user1", "pass1", "email");
        facade.register(regRequest);
        LoginRequest request = new LoginRequest("user1", "pass1");
        LoginResult result = facade.login(request);
        assert result.username().equals("user1");
    }

    @Test
    public void failedLoginTest() {
        try {
            RegisterRequest regRequest = new RegisterRequest("user2", "pass2", "email");
            facade.register(regRequest);
            LoginRequest request = new LoginRequest("user2", "wrongPassword");
            facade.login(request);
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
            assert e.getMessage().equals("Cannot invoke \"java.lang.Double.intValue()\" because the return value of \"java.util.HashMap.get(Object)\" is null");
        }
    }

    @Test
    public void logoutTest() throws ResponseException {
        RegisterRequest regRequest = new RegisterRequest("user3", "pass3", "email");
        facade.register(regRequest);
        LoginRequest request = new LoginRequest("user3", "pass3");
        LoginResult login = facade.login(request);
        AuthRequest logRequest = new AuthRequest(login.authToken());
        LogoutResult result = facade.logout(logRequest);
        LogoutResult check = new LogoutResult();
        assert result.equals(check);
    }

    @Test
    public void failedLogoutTest() {
        AuthRequest request = new AuthRequest("");
        try {
            facade.logout(request);
        } catch (ResponseException e) {
            assert e.getMessage().equals("Cannot invoke \"java.lang.Double.intValue()\" because the return value of \"java.util.HashMap.get(Object)\" is null");
        }
    }

    @Test
    public void listGamesEmptyTest() throws ResponseException {
        RegisterRequest reg = new RegisterRequest("you", "me", "yes");
        RegisterResult regResult = facade.register(reg);
        AuthRequest request = new AuthRequest(regResult.authToken());
        ListResult result = facade.listGames(request);
        assert  result.games().isEmpty();
    }

}
