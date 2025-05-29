package vivetapp.model;

public class Message {
    private Long id;
    private Long senderId;
    private Long chatId;
    private String content;
    private String timestamp;

    public Message() {}

    public Message(Long id, Long senderId, Long chatId, String content, String timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.chatId = chatId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public Long getSenderId() { return senderId; }
    public Long getChatId() { return chatId; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }

    public void setId(Long id) { this.id = id; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public void setChatId(Long recipientId) { this.chatId = recipientId; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
