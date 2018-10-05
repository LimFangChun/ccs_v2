package my.edu.tarc.communechat_v2.model;

public class Participant {
    private int room_id;
    private int user_id;
    private String role;

    public Participant(){

    }

    public Participant(int room_id, int user_id, String role) {
        this.room_id = room_id;
        this.user_id = user_id;
        this.role = role;
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
}
