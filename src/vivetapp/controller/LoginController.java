package com.example.messengerclient.controller;

import com.example.messengerclient.service.AuthService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label statusLabel;

    private AuthService authService = new AuthService();

    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password");
            return;
        }

        new Thread(() -> {
            try {
                String token = authService.login(username, password);
                Platform.runLater(() -> {
                    try {
                        openChatWindow(token, username);
                    } catch (Exception e) {
                        statusLabel.setText("Failed to open chat: " + e.getMessage());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Login failed: " + e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void register() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password");
            return;
        }

        new Thread(() -> {
            try {
                String response = authService.register(username, password);
                Platform.runLater(() -> {
                    if (response.contains("successfully")) {
                        statusLabel.setText("Registration successful. Please login.");
                    } else {
                        statusLabel.setText(response);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Registration failed: " + e.getMessage()));
            }
        }).start();
    }

    private void openChatWindow(String token, String username) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/messengerclient/chat.fxml"));
        Parent root = loader.load();

        ChatController chatController = loader.getController();
        chatController.init(token, username);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Messenger - " + username);
        stage.show();
    }
}