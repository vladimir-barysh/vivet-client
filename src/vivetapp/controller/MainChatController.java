package vivetapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import vivetapp.model.ChatGroup;
import vivetapp.model.Message;
import vivetapp.service.ChatService;
import vivetapp.service.GroupService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

public class MainChatController {
    private String token;
    private String email;
    private Long chatId;
    private String chatName;
    private GroupService groupService;
    private ChatService chatService;
    private Timer refreshTimer;
    private Timer messageRefreshTimer;
    private static final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); // ISO без миллисекунд
    private static final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
    private final Map<Long, String> userCache = new HashMap<>();

    public void init(String token, String email) {
        this.token = token;
        this.email = email;
        this.chatService = new ChatService();
        this.groupService = new GroupService();

        setupContextMenu();
        loadChats();
        startRefreshTimer();
    }

    @FXML private Label accessableLabel;
    @FXML private ListView chatListView;
    @FXML private Label chatWith;
    @FXML private TextArea chatArea;
    @FXML private TextField messageField;
    @FXML
    private void logOut() {
        stopRefresh();
        stopMessageRefreshTimer();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vivetapp/fxml/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) chatListView.getScene().getWindow();
            Image icon = new Image(getClass().getResourceAsStream("/vivetapp/resources/icon.png"));
            stage.getIcons().add(icon);
            stage.setTitle("Vivet. Авторизация");
            stage.setResizable(false);
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onCreateChat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vivetapp/fxml/createchat.fxml"));
            Parent root = loader.load();

            CreateChatController controller = loader.getController();
            controller.init(token);

            Stage stageCreate = new Stage();
            Image icon = new Image(getClass().getResourceAsStream("/vivetapp/resources/icon.png"));
            stageCreate.getIcons().add(icon);
            stageCreate.setTitle("Vivet. Создание нового чата");
            stageCreate.setScene(new Scene(root));
            stageCreate.setResizable(false);
            stageCreate.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void sendMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty()) return;

        Message message = new Message();
        message.setChatId(chatId);
        message.setContent(content);

        boolean sent = chatService.sendMessage(token, message);
        if (sent) {
            messageField.clear();
            chatArea.appendText("Вы: " + content + "\n");
        } else {
            chatArea.appendText("Ошибка при отправке сообщения\n");
        }
    }

    private void setupContextMenu() {
        chatListView.setCellFactory(lv -> {
            ListCell<ChatGroup> cell = new ListCell<>() {
                @Override
                protected void updateItem(ChatGroup item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getGroupName());
                }
            };

            ContextMenu menu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Удалить чат");
            deleteItem.setOnAction(e -> {
                ChatGroup group = cell.getItem();
                if (group != null) deleteChat(group.getGroupId());
            });
            menu.getItems().add(deleteItem);

            cell.setOnContextMenuRequested((ContextMenuEvent e) -> {
                if (!cell.isEmpty()) {
                    menu.show(cell, e.getScreenX(), e.getScreenY());
                }
            });

            cell.setOnMouseClicked((MouseEvent e) -> {
                if (e.getClickCount() == 2 && !cell.isEmpty()) {
                    ChatGroup selected = cell.getItem();
                    openChat(selected);
                }
            });

            return cell;
        });
    }

    private void deleteChat(Long chatId) {
        try {
            URL url = new URL("http://45.153.69.122:8080/api/chats/" + chatId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            int code = conn.getResponseCode();
            if (code == 200) {
                loadChats();
            } else {
                System.out.println("Ошибка удаления: " + code);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void startMessageRefreshTimer() {
        if (messageRefreshTimer != null) {
            messageRefreshTimer.cancel();
        }

        messageRefreshTimer = new Timer(true);
        messageRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (chatId != null) {
                    loadChat(chatId);
                }
            }
        }, 0, 3000); // каждые 3 секунды
    }

    private void stopMessageRefreshTimer() {
        if (messageRefreshTimer != null) {
            messageRefreshTimer.cancel();
            messageRefreshTimer = null;
        }
    }

    private void loadChats() {
        List<ChatGroup> groups = groupService.getUserGroups(token);
        Platform.runLater(() -> chatListView.getItems().setAll(groups));
    }

    private void startRefreshTimer() {
        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                loadChats();
            }
        }, 0, 5000); // каждые 5 секунд
    }

    public void stopRefresh() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
    }



    private void openChat(ChatGroup chatGroup) {
        if (chatGroup == null) return;
        this.chatId = chatGroup.getGroupId();
        this.chatName = chatGroup.getGroupName();

        chatWith.setText(chatName);
        stopMessageRefreshTimer();
        loadChat(chatId);
        startMessageRefreshTimer();
    }

    public void loadChat(Long chatId) {
        try {
            URL url = new URL("http://45.153.69.122:8080/api/chat/messages/" + chatId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                ObjectMapper mapper = new ObjectMapper();
                Message[] messages = mapper.readValue(inputStream, Message[].class);

                Platform.runLater(() -> {
                    chatArea.clear();
                    for (Message msg : messages) {
                        displayMessage(msg);
                    }
                });
            } else {
                System.out.println("Не удалось загрузить сообщения. Код: " + conn.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void displayMessage(Message message) {
        LocalDateTime timestamp = LocalDateTime.parse(message.getTimestamp(), inputFormatter);
        String formattedTime = timestamp.format(outputFormatter);

        String senderName = getUsernameCached(message.getSenderId());

        String text = String.format("[%s] %s: %s\n", formattedTime, senderName, message.getContent());
        chatArea.appendText(text);
    }

    private String getUsernameCached(Long userId) {
        if (userCache.containsKey(userId)) {
            return userCache.get(userId);
        }

        try {
            URL url = new URL("http://45.153.69.122:8080/api/users/" + userId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String username = reader.readLine();
                userCache.put(userId, username);
                return username;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "неизвестный";
    }

}
