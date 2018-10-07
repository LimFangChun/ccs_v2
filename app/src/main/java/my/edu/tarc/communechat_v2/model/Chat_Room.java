package my.edu.tarc.communechat_v2.model;

import java.util.Date;

public class Chat_Room {
    //variables that define column name
    public static final String COL_ROOM_ID = "room_id";
    public static final String COL_OWNER_ID = "owner_id";
    public static final String COL_ROOM_NAME = "room_name";
    public static final String COL_DATE_CREATED = "date_created";
    public static final String COL_LAST_UPDATE = "last_update";
    public static final String COL_TOPIC_ADDRESS = "topic_address";

    //variables for encapsulation
    private int room_id;
    private int owner_id;
    private String room_name;
    private Date date_created;
    private Date last_update;
    private String topic_address;

    public Chat_Room(){

    }

    public Chat_Room(int room_id, int owner_id, String room_name, Date date_created, Date last_update, String topic_address) {
        this.room_id = room_id;
        this.owner_id = owner_id;
        this.room_name = room_name;
        this.date_created = date_created;
        this.last_update = last_update;
        this.topic_address = topic_address;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public Date getLast_update() {
        return last_update;
    }

    public void setLast_update(Date last_update) {
        this.last_update = last_update;
    }

    public String getTopic_address() {
        return topic_address;
    }

    public void setTopic_address(String topic_address) {
        this.topic_address = topic_address;
    }
}
