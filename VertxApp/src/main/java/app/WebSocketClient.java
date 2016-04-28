package app;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class WebSocketClient {

    public static void main(String[] args) throws URISyntaxException {

        ClientWebSocketEndpoint clientEndpoint = new ClientWebSocketEndpoint(new URI("ws://localhost:8090/socket"));
        clientEndpoint.addMessageHandler(data -> System.out.println("Message from server: " + data));

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String message = scanner.nextLine();
                clientEndpoint.sendMessage(message);
            }
        }
    }


}
