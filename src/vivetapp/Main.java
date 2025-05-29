package vivetapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/vivetapp/fxml/login.fxml"));
        Image icon = new Image(getClass().getResourceAsStream("/vivetapp/resources/icon.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Vivet. Авторизация");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}