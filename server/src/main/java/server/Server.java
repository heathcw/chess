package server;

import com.google.gson.Gson;
import com.sun.security.jgss.AuthorizationDataEntry;
import dataaccess.DataAccessException;
import handler.HandlerClass;
import spark.*;

public class Server {

    private HandlerClass handler = new HandlerClass();

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

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
        return handler.registerHandler(req.body());
    }

    private Object login(Request req, Response res) {
        return handler.loginHandler(req.body());
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        try {
            return handler.logoutHandler(req.headers("Authorization"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        return handler.listGamesHandler(req.headers("Authorization"));
    }

    private Object createGame(Request req, Response res) throws DataAccessException {
        return handler.createGameHandler(req.body(), req.headers("Authorization"));
    }

    private Object joinGame(Request req, Response res) throws DataAccessException {
        return handler.joinGameHandler(req.body(), req.headers("Authorization"));
    }

    private Object clear(Request req, Response res) {
        return handler.clearHandler();
    }
}
