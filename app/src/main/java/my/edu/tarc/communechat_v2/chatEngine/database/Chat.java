package my.edu.tarc.communechat_v2.chatEngine.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Chat {

    public static final String TEXT_MESSAGE = "TextMessage";
    public static final String IMAGE_MESSAGE = "ImageMessage";

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long roomId;
    private String message;
    private String date;
    private String comparingDateTime;
    private String senderId;
    private String messageType;
    private String chatRoomUniqueTopic;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getChatRoomUniqueTopic() {
        return chatRoomUniqueTopic;
    }

    public void setChatRoomUniqueTopic(String chatRoomUniqueTopic) {
        this.chatRoomUniqueTopic = chatRoomUniqueTopic;
    }

    public String getComparingDateTime() {
        return comparingDateTime;
    }

    public void setComparingDateTime(String comparingDateTime) {
        this.comparingDateTime = comparingDateTime;
    }
}
