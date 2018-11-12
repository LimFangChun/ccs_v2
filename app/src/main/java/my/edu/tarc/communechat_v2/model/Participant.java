package my.edu.tarc.communechat_v2.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Participant extends User {
    //variables that define column name
    public static final String COL_ROOM_ID = "room_id";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_ROLE = "role";

    //variables for encapsulation
    private int room_id;
    private int user_id;
    private String role;
    private Calendar join_date;

    public Participant(){

    }

    public Participant(int room_id, int user_id, String role, Calendar join_date) {
        this.room_id = room_id;
        this.user_id = user_id;
        this.role = role;
        this.join_date = join_date;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Calendar getJoin_date() {
        return join_date;
    }

    public void setJoin_date(Calendar join_date) {
        this.join_date = join_date;
    }

    public void setJoin_date(String join_date) {
        try {
            join_date = join_date.replace("T", " ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            this.join_date.setTime(dateFormat.parse(join_date));
        } catch (NullPointerException | ParseException e) {
            e.printStackTrace();
        }
    }
}
