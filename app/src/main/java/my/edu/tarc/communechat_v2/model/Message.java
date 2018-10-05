package my.edu.tarc.communechat_v2.model;

import java.util.Date;

public class Message {
    //variables that define column name
    public static final String COL_MESSAGE_ID = "message_id";
    public static final String COL_MESSAGE = "message";
    public static final String COL_SENDER_ID = "sender_id";
    public static final String COL_DATE_CREATED = "date_created";
    public static final String COL_ROOM_ID = "room_id";
    public static final String COL_MESSAGE_TYPE = "message_type";
    public static final String COL_STATUS = "status";

    //variables for encapsulation
    private int message_id;
    private String message;
    private int sender_id;
    private Date date_created;
    private int room_id;
    private String message_type;
    private String status;

    public Message(){}

    public Message(int message_id, String message, int sender_id, Date date_created, int room_id, String message_type, String status) {
        this.message_id = message_id;
        this.message = message;
        this.sender_id = sender_id;
        this.date_created = date_created;
        this.room_id = room_id;
        this.message_type = message_type;
        this.status = status;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
