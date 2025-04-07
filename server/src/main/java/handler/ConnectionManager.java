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
    public final ConcurrentHashMap<Integer, ArrayList<Connection>> connections = new ConcurrentHashMap<>();
    private final Gson serializer = new Gson();

    public void add(int id, String user, Session session) {
        var connection = new Connection(user, session);
        if (connections.containsKey(id)) {
            connections.get(id).add(connection);
        } else {
            connections.put(id, new ArrayList<>());
            connections.get(id).add(connection);
        }
    }

    public void remove(Integer id, String user) {
        connections.get(id).removeIf(c -> c.user.equals(user));
    }

    public void broadcast(Integer id, String excludeUser, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (Connection c : connections.get(id)) {
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
            connections.get(id).remove(c);
        }
    }

    public void load(Integer id, LoadGameMessage message, String includeUser) throws IOException {
        for (Connection c : connections.get(id)) {
            if (c.session.isOpen()) {
                if (c.user.equals(includeUser)) {
                    String send = serializer.toJson(message);
                    c.send(send);
                }
            }
        }
    }

    public void error(Session session, ErrorMessage e) throws IOException {
        String send = serializer.toJson(e);
        session.getRemote().sendString(send);
    }

    public void notification(Integer id, NotificationMessage message, String user) throws IOException {
        String send = serializer.toJson(message);
        for (Connection c : connections.get(id)) {
            if (c.session.isOpen()) {
                if (c.user.equals(user)) {
                    c.send(send);
                }
            }
        }
    }
}
