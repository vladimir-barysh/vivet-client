package vivetapp.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    @FXML
    private void onRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Пароли не совпадают");
            return;
        }

        try {
            URL url = new URL("http://45.153.69.122:8080/api/auth/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = String.format(
                    "{\"username\":\"%s\", \"email\":\"%s\", \"password\":\"%s\"}",
                    username, email, password
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            if (conn.getResponseCode() == 200) {
                try {
                    URL urlLogin = new URL("http://45.153.69.122:8080/api/auth/login");
                    HttpURLConnection connection = (HttpURLConnection) urlLogin.openConnection();
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
                        errorLabel.setText("Ошибка входа: " + responseCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    errorLabel.setText("Ошибка подключения к серверу");
                }
            } else {
                if (conn.getResponseCode() == 400){
                    errorLabel.setText("Пользователь с такой почтой и именем уже зарегистрирован");
                }
                else {
                    errorLabel.setText("Ошибка: " + conn.getResponseCode());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Ошибка подключения к серверу");
        }
    }

    @FXML
    private void onBack() {
        loadLoginScene();
    }

    private void loadLoginScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vivetapp/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Image icon = new Image(getClass().getResourceAsStream("/vivetapp/resources/icon.png"));
            stage.getIcons().add(icon);
            stage.setTitle("Vivet. Авторизация");
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
            stage.show();
            stage.setOnCloseRequest(e -> controller.stopRefresh());
            ((Stage) emailField.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

