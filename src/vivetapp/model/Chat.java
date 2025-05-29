package vivetapp.model;

public class Chat {
    private Long groupId;
    private Long chatId;

    public Chat(){ }
    public Chat(Long groupId, Long chatId){
        this.groupId = groupId;
        this.chatId = chatId;
    }
    public Long getGroupId() { return groupId; }
    public Long getChatId() { return chatId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }

}
