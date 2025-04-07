package server;

import com.google.gson.Gson;
import com.sun.security.jgss.AuthorizationDataEntry;
import dataaccess.DataAccessException;
import handler.HandlerClass;
import handler.WebSocketHandler;
import spark.*;

public class Server {

    private final HandlerClass handler = new HandlerClass();
    private final WebSocketHandler wsHandler = new WebSocketHandler();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", wsHandler);

        // Register your endpoints and handle exceptions here.
        createRoutes();

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void createRoutes() {
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
    }

    private Object register(Request req, Response res) {
        try {
            return handler.registerHandler(req.body());
        } catch (DataAccessException e) {
            return handler.exceptionHandler(res, e);
        }
    }

    private Object login(Request req, Response res) {
        try {
            return handler.loginHandler(req.body());
        } catch (DataAccessException e) {
            return handler.exceptionHandler(res, e);
        }
    }

    private Object logout(Request req, Response res) {
        try {
            return handler.logoutHandler(req.headers("Authorization"));
        } catch (DataAccessException e) {
            return handler.exceptionHandler(res, e);
        }
    }

    private Object listGames(Request req, Response res) {
        try {
            return handler.listGamesHandler(req.headers("Authorization"));
        } catch (DataAccessException e) {
            return handler.exceptionHandler(res, e);
        }
    }

    private Object createGame(Request req, Response res) {
        try {
            return handler.createGameHandler(req.body(), req.headers("Authorization"));
        } catch (DataAccessException e) {
            return handler.exceptionHandler(res, e);
        }
    }

    private Object joinGame(Request req, Response res) {
        try {
            return handler.joinGameHandler(req.body(), req.headers("Authorization"));
        } catch (DataAccessException e) {
            return handler.exceptionHandler(res, e);
        }
    }

    private Object clear(Request req, Response res) {
        return handler.clearHandler();
    }
}
