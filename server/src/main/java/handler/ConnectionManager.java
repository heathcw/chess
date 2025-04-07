package handler;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();
    private final Gson serializer = new Gson();

    public void add(String user, Session session) {
        var connection = new Connection(user, session);
        connections.put(user, connection);
    }

    public void remove(String user) {
        connections.remove(user);
    }

    public void broadcast(String excludeUser, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.user.equals(excludeUser)) {
                    String send = serializer.toJson(message);
                    c.send(send);
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.user);
        }
    }

    public void load(LoadGameMessage message, String includeUser) throws IOException {
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.user.equals(includeUser)) {
                    String send = serializer.toJson(message);
                    c.send(send);
                }
            }
        }
    }

    public void error(Session session, ErrorMessage e, String user) throws IOException {
        String send = serializer.toJson(e);
        if (user == null) {
            session.getRemote().sendString(send);
        }
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.user.equals(user)) {
                    c.send(send);
                    connections.remove(c.user);
                }
            }
        }
    }
}
