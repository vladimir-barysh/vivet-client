package vivetapp.service;

import vivetapp.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean sendMessage(String token, Message message) {
        try {
            URL url = new URL("http://45.153.69.122:8080/api/chat/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = objectMapper.writeValueAsString(message);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
