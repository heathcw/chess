package websocket.messages;

public class LoadGameMessage extends ServerMessage {

    private final String game;

    public LoadGameMessage(String game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public String getGame() {return this.game;}
}
