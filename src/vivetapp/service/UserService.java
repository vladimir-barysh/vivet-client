package vivetapp.service;

import vivetapp.model.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class UserService {

    public UserService(){ }
    public List<User> getAllUsers(String token) {
        try {
            URL url = new URL("http://45.153.69.122:8080/api/users");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                ObjectMapper mapper = new ObjectMapper();
                return Arrays.asList(mapper.readValue(is, User[].class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public User getCurrentUser(String token) {
        try {
            URL url = new URL("http://45.153.69.122:8080/api/users/me");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            if (conn.getResponseCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(conn.getInputStream(), User.class); // или CurrentUserDto
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
