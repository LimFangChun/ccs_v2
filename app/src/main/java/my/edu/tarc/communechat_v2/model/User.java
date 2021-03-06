package my.edu.tarc.communechat_v2.model;

import android.annotation.SuppressLint;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Xeosz on 26-Sep-17.
 */

@Entity
public class User {
    //static final variables that define column name
    public static final String COL_USER_ID = "user_id";
    public static final String COL_USERNAME = "username";
    public static final String COL_DISPLAY_NAME = "display_name";
    public static final String COL_PASSWORD = "password";
    public static final String COL_POSITION = "position";
    public static final String COL_GENDER = "gender";
    public static final String COL_NRIC = "nric";
    public static final String COL_DATE_OF_BIRTH = "date_of_birth";
    public static final String COL_PHONE_NUMBER = "phone_number";
    public static final String COL_EMAIL = "email";
    public static final String COL_ADDRESS = "address";
    public static final String COL_CITY_ID = "city_id";
    public static final String COL_STATUS = "status";
    public static final String COL_LAST_ONLINE = "last_online";
    public static final String COL_LAST_LONGITUDE = "last_longitude";
    public static final String COL_LAST_LATITUDE = "last_latitude";
    public static final String COL_DISTANCE = "distance";
	public static final String COL_PUBLIC_KEY = "public_key";
	public static final String COL_PRIVATE_KEY = "private_key";
    public static final String GENDER_MALE = "M";
    public static final String GENDER_FEMALE = "F";
    public static final String POSITION_STUDENT = "Student";
    public static final String POSITION_LECTURER = "Lecturer";
    public static final String SIMPLE_DATE_FORMAT = "dd/MM/yy";
    public static final String SQL_DATE_FORMAT = "yyyy-MM-dd";

    //variables for encapsulation
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    private int user_id;
    private String username;
    private String display_name;
    private String password;
    private String position;
    private String gender;
    private String nric;
    private Calendar dateOfBirth;
    private String phone_number;
    private String email;
    private String address;
    private String city_id;
    private String status;
    private Calendar last_online;
    private double last_longitude;
    private double last_latitude;
    private double distance;
    private String public_key;
    private byte[] profile_picture;

    public  User(){
        last_online = Calendar.getInstance();
    }

    public User(int user_id, String username, String display_name, String password, String position,
                String gender, String nric, String phone_number, String email, String address,
                String city_id, String status, Calendar last_online, double last_longitude, double last_latitude,
                String public_key) {
        this.user_id = user_id;
        this.username = username;
        this.display_name = display_name;
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
        this.last_longitude = last_longitude;
        this.last_latitude = last_latitude;
        this.public_key=public_key;
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

    public Calendar getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Calendar dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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

    public String getStatusInUnicode() {
        if (getStatus().equals("Offline")) {
            //red button
            return "\uD83D\uDD34";
        } else {
            //blue button
            return "\uD83D\uDD35";
        }
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Calendar getLast_online() {
        return last_online;
    }

    public void setLast_online(Calendar last_online) {
        this.last_online = last_online;
    }

    //i dont know what is this suppressLint thing
    //it gives me error if i dont put it
    @SuppressLint("NewApi")
    public void setLast_online(String last_online) {
        try{
            last_online = last_online.replace("T", " ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            this.last_online.setTime(dateFormat.parse(last_online));
        }catch (NullPointerException|ParseException e){
            e.printStackTrace();
        }
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public double getLast_longitude() {
        return last_longitude;
    }

    public void setLast_longitude(double last_longitude) {
        this.last_longitude = last_longitude;
    }

    public double getLast_latitude() {
        return last_latitude;
    }

    public void setLast_latitude(double last_latitude) {
        this.last_latitude = last_latitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String calculateLastOnline(){
        long lastOnlineAgo = getLast_online().getTimeInMillis() - System.currentTimeMillis();
        if (lastOnlineAgo / 1000 / 60 / 60 / 24 / 30 != 0) {
            return Math.abs(lastOnlineAgo / 1000 / 60 / 60 / 24 / 30) + " month(s) ago";
        } else if (lastOnlineAgo / 1000 / 60 / 60 / 24 != 0) {
            return  Math.abs(lastOnlineAgo / 1000 / 60 / 60 / 24) + " day(s) ago";
        } else if (lastOnlineAgo / 1000 / 60 / 60 != 0) {
            return  Math.abs(lastOnlineAgo / 1000 / 60 / 60) + " hour(s) ago";
        } else if (lastOnlineAgo / 1000 / 60 != 0) {
            return Math.abs(lastOnlineAgo / 1000 / 60) + " minute(s) ago";
        } else {
            return Math.abs(lastOnlineAgo / 1000) + " second(s) ago";
        }
    }

    public String formatLastOnline() {
        return getLast_online().get(Calendar.DAY_OF_MONTH) + "/" +
                getLast_online().get(Calendar.MONTH) + "/" +
                getLast_online().get(Calendar.YEAR);
    }

    public String getPublic_key() {
        return public_key;
    }

    public void setPublic_key(String public_key) {
        this.public_key = public_key;
    }

    public byte[] getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(byte[] profile_picture) {
        this.profile_picture = profile_picture;
    }

    public Bitmap getProfilePictureBitmap() {
        return BitmapFactory.decodeByteArray(profile_picture, 0, profile_picture.length);
    }

    public void setDobFromString(String dob, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
        try {
            dateOfBirth = Calendar.getInstance();
            dateOfBirth.setTime(sdf.parse(dob));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public String getDobString(String format){
        String dob = "";

        if(dateOfBirth!=null){
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);

            dob = sdf.format(dateOfBirth.getTime());
        }
        return dob;
    }

}
