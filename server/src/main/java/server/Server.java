package server;

import com.google.gson.Gson;
import handler.HandlerClass;
import spark.*;

public class Server {

    private HandlerClass handler = new HandlerClass();
    private Gson serializer = new Gson();

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
        Spark.post("/user", this::register);
    }

    private Object register(Request req, Response res) {
        return handler.registerHandler(req.body());
    }
}
