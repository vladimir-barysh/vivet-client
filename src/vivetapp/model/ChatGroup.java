package vivetapp.model;

public class ChatGroup {
    private Long groupId;
    private String groupName;

    public ChatGroup(){ }
    public ChatGroup(Long groupId, String groupName){
        this.groupId = groupId;
        this.groupName = groupName;
    }
    public Long getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    @Override
    public String toString() {
        return groupName;
    }
}

