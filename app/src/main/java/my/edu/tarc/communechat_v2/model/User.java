package my.edu.tarc.communechat_v2.model;

import java.util.Date;

/**
 * Created by Xeosz on 26-Sep-17.
 */

public class User {
    //static final variables that define column name
    public static final String COL_USER_ID = "user_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_POSITION = "position";
    public static final String COL_GENDER = "gender";
    public static final String COL_NRIC = "nric";
    public static final String COL_PHONE_NUMBER = "phone_number";
    public static final String COL_EMAIL = "email";
    public static final String COL_ADDRESS = "address";
    public static final String COL_CITY_ID = "city_id";
    public static final String COL_STATUS = "status";
    public static final String COL_LAST_ONLINE = "last_online";

    //variables for encapsulation
    private int user_id;
    private String username;
    private String password;
    private String position;
    private String gender;
    private String nric;
    private String phone_number;
    private String email;
    private String address;
    private String city_id;
    private String status;
    private Date last_online;

    public  User(){

    }

    public User(int user_id, String username, String password, String position, String gender,
                String nric, String phone_number, String email, String address, String city_id,
                String status, Date last_online) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.position = position;
        this.gender = gender;
        this.nric = nric;
        this.phone_number = phone_number;
        this.email = email;
        this.address = address;
        this.city_id = city_id;
        this.status = status;
        this.last_online = last_online;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLast_online() {
        return last_online;
    }

    public void setLast_online(Date last_online) {
        this.last_online = last_online;
    }
}
