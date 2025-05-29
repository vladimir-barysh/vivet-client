package vivetapp.service;

import vivetapp.model.ChatGroup;
import vivetapp.request.CreateGroupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupService {

    public boolean createGroup(String token, String groupName, List<Long> userIds) {
        try {
            URL url = new URL("http://45.153.69.122:8080/api/groups/create");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            CreateGroupRequest request = new CreateGroupRequest(groupName, userIds);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(request);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            int responseCode = conn.getResponseCode();
            return responseCode == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ChatGroup> getUserGroups(String token) {
        try {
            URL url = new URL("http://45.153.69.122:8080/api/groups/groups");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            if (conn.getResponseCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                InputStream is = conn.getInputStream();
                return Arrays.asList(mapper.readValue(is, ChatGroup[].class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}

