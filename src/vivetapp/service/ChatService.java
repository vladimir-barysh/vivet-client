package vivetapp.services;

import vivetapp.model.Message;

import java.net.URI;
import java.util.function.Consumer;

public class ChatService extends WebSocketClient {

    private Consumer<Message> onMessageCallback;

    public ChatService(URI serverUri) {
        super(serverUri);
    }

    public void setOnMessageReceived(Consumer<Message> callback) {
        this.onMessageCallback = callback;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("✅ Connected to WebSocket");
    }

    @Override
    public void onMessage(String messageJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Message msg = mapper.readValue(messageJson, Message.class);
            if (onMessageCallback != null) {
                onMessageCallback.accept(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message msg) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            send(mapper.writeValueAsString(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
