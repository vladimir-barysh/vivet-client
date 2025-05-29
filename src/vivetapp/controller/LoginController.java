package vivetapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML
    private void onLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Введите логин и пароль");
            return;
        }

        try {
            URL url = new URL("http://45.153.69.122:8080/api/auth/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonBody = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(jsonBody.getBytes());
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                Scanner scanner = new Scanner(connection.getInputStream());
                String response = scanner.useDelimiter("\\A").next();
                scanner.close();

                // Предполагаем, что токен в ответе как: "Login successful. Token: <token>"
                String token = response.split("Token: ")[1].trim();
                System.out.println("token: " + token);

                openApp(token, email);
            } else {
                if (responseCode == 404) {
                    statusLabel.setText("Неверный логин или пароль");
                } else {
                    statusLabel.setText("Ошибка входа: " + responseCode);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Ошибка подключения к серверу");
        }
    }
    @FXML
    private void onOpenRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vivetapp/fxml/register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            Image icon = new Image(getClass().getResourceAsStream("/vivetapp/resources/icon.png"));
            stage.getIcons().add(icon);
            stage.setTitle("Vivet. Регистрация");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openApp(String token, String email){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vivetapp/fxml/mainchat.fxml"));
            Parent root = loader.load();

            MainChatController controller = loader.getController();
            controller.init(token, email);

            Stage stage = new Stage();
            Image icon = new Image(getClass().getResourceAsStream("/vivetapp/resources/icon.png"));
            stage.getIcons().add(icon);
            stage.setTitle("Vivet");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(e -> controller.stopRefresh());
            ((Stage) emailField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
