package ui;

import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessRepl implements NotificationHandler {

    private final ChessClient client;

    public ChessRepl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> {
                System.out.println(((NotificationMessage) message).getMessage());
                printPrompt();
            }
            case LOAD_GAME -> {
                System.out.println(client.redraw(((LoadGameMessage) message).getGame()));
                printPrompt();
            }
            case ERROR -> {
                System.out.println(((ErrorMessage) message).getErrorMessage());
                printPrompt();
            }
        }
    }
}
