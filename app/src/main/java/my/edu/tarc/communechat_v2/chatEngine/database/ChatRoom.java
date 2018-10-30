package my.edu.tarc.communechat_v2.chatEngine.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ChatRoom {

    @Ignore
    public static final String PRIVATE_CHAT_ROOM = "PrivateChatRoom";
    @Ignore
    public static final String GROUP_CHAT_ROOM = "GroupChatRoom";

    @Ignore
    public static final String ACTIVE_STATUS = "ActiveStatus";
    @Ignore
    public static final String LEFT_STATUS = "LeftStatus";
    @Ignore
    public static final String CHAT_ROOM_JOINED = "Joined";
    @Ignore
    public static final String CHAT_ROOM_LEFT = "Left";

    @Ignore
    public static final String GROUP_DIVIDER = "XTX";

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String chatRoomUniqueTopic;
    private String name;
    private String latestMessage;
    private String dateTimeMessageReceived;
    private String chatRoomType; //Private or Group
    private String status; //Like joined or left
    private String groupMember;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public String getDateTimeMessageReceived() {
        return dateTimeMessageReceived;
    }

    public void setDateTimeMessageReceived(String dateTimeMessageReceived) {
        this.dateTimeMessageReceived = dateTimeMessageReceived;
    }

    public String getChatRoomUniqueTopic() {
        return chatRoomUniqueTopic;
    }

    public void setChatRoomUniqueTopic(String chatRoomUniqueTopic) {
        this.chatRoomUniqueTopic = chatRoomUniqueTopic;
    }

    public String getChatRoomType() {
        return chatRoomType;
    }

    public void setChatRoomType(String chatRoomType) {
        this.chatRoomType = chatRoomType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(String groupMember) {
        this.groupMember = groupMember;
    }
}
