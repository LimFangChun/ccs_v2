package my.edu.tarc.communechat_v2.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Friendship {
    //variables that define column name
    public static final String COL_USER_ID = "user_id";
    public static final String COL_FRIEND_ID = "friend_id";
    public static final String COL_STATUS = "status";
    public static final String COL_DATE_CREATED = "date_created";
    public static final String COL_SENDER_ID = "sender_id";

    //variables for encapsulation
    private int user_id;
    private int friend_id;
    private String status;
    private Date date_created;
    private int sender_id;

    public Friendship(){

    }

    public Friendship(int user_id, int friend_id, String status, Date date_created, int sender_id) {
        this.user_id = user_id;
        this.friend_id = friend_id;
        this.status = status;
        this.date_created = date_created;
        this.sender_id = sender_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(int friend_id) {
        this.friend_id = friend_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public void setDateCreated(String date_created){
        try{
            date_created = date_created.replace("T", " ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            this.date_created = (Date) dateFormat.parse(date_created);
        }catch (NullPointerException|ParseException e){
            e.printStackTrace();
        }
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }
}
