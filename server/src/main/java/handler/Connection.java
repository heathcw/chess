package handler;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public String user;
    public Session session;

    public Connection(String user, Session session) {
        this.user = user;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
