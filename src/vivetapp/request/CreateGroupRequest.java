package vivetapp.request;

import java.util.List;

public class CreateGroupRequest {
    private String groupName;
    private List<Long> userIds;

    public CreateGroupRequest(String groupName, List<Long> userIds) {
        this.groupName = groupName;
        this.userIds = userIds;
    }
    public String getGroupName() { return groupName; }
    public List<Long> getUserIds() { return userIds; }
}