package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import service.RegisterRequest;
import service.RegisterResult;
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

}
