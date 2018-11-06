package my.edu.tarc.communechat_v2.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import my.edu.tarc.communechat_v2.ADT.CryptoDecryptInterface;
import my.edu.tarc.communechat_v2.ADT.CryptoEncryptInterface;

public class Chat_Room {
    //variables that define column name
    public static final String COL_ROOM_ID = "room_id";
    public static final String COL_OWNER_ID = "owner_id";
    public static final String COL_ROOM_NAME = "room_name";
    public static final String COL_DATE_CREATED = "date_created";
    public static final String COL_LAST_UPDATE = "last_update";
    public static final String COL_TOPIC_ADDRESS = "topic_address";
    public static final String COL_SECRET_KEY = "secret_key";

    //variables for encapsulation
    private int room_id;
    private int owner_id;
    private String room_name;
    private Calendar date_created;
    private Calendar last_update;
    private String topic_address;
    private String role;

    private byte[] secret_key;

    public Chat_Room(){
        date_created = Calendar.getInstance();
        last_update = Calendar.getInstance();
    }

    public Chat_Room(int room_id, int owner_id, String room_name, Calendar date_created, Calendar last_update, String topic_address) {
        this.room_id = room_id;
        this.owner_id = owner_id;
        this.room_name = room_name;
        this.date_created = date_created;
        this.last_update = last_update;
        this.topic_address = topic_address;
    }

    public Chat_Room(int room_id, byte[] secret_key){
        //constructor for room
        this.room_id = room_id;
        this.secret_key = secret_key;
    }

    public Chat_Room(int room_id, int owner_id, String room_name, Calendar date_created, Calendar last_update, String topic_address, String role, byte[] secret_key) {
        this.room_id = room_id;
        this.owner_id = owner_id;
        this.room_name = room_name;
        this.date_created = date_created;
        this.last_update = last_update;
        this.topic_address = topic_address;
        this.role = role;
        this.secret_key = secret_key;
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

    public Calendar getDate_created() {
        return date_created;
    }

    public String formatDateCreated() {
        return getDate_created().get(Calendar.DAY_OF_MONTH) + "/" +
                getDate_created().get(Calendar.MONTH) + "/" +
                getDate_created().get(Calendar.YEAR);
    }

    public void setDate_created(Calendar date_created) {
        this.date_created = date_created;
    }

    public void setDate_created(String date_created) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            this.date_created.setTime(dateFormat.parse(date_created));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Calendar getLast_update() {
        return last_update;
    }

    public void setLast_update(Calendar last_update) {
        this.last_update = last_update;
    }

    public void setLast_update(String last_update) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            this.last_update.setTime(dateFormat.parse(last_update));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getTopic_address() {
        return topic_address;
    }

    public void setTopic_address(String topic_address) {
        this.topic_address = topic_address;
    }

    public String calculateLastUpdate() {
        long lastOnlineAgo = getLast_update().getTimeInMillis() - System.currentTimeMillis();
        if (lastOnlineAgo / 1000 / 60 / 60 / 24 / 30 != 0) {
            return Math.abs(lastOnlineAgo / 1000 / 60 / 60 / 24 / 30) + " month(s) ago";
        } else if (lastOnlineAgo / 1000 / 60 / 60 / 24 != 0) {
            return Math.abs(lastOnlineAgo / 1000 / 60 / 60 / 24) + " day(s) ago";
        } else if (lastOnlineAgo / 1000 / 60 / 60 != 0) {
            return Math.abs(lastOnlineAgo / 1000 / 60 / 60) + " hour(s) ago";
        } else if (lastOnlineAgo / 1000 / 60 != 0) {
            return Math.abs(lastOnlineAgo / 1000 / 60) + " minute(s) ago";
        } else {
            return Math.abs(lastOnlineAgo / 1000) + " second(s) ago";
        }
    }

    public String calculateDateCreated() {
        long dateCreated = getDate_created().getTimeInMillis() - System.currentTimeMillis();
        if (dateCreated / 1000 / 60 / 60 / 24 / 30 != 0) {
            return Math.abs(dateCreated / 1000 / 60 / 60 / 24 / 30) + " month(s) ago";
        } else if (dateCreated / 1000 / 60 / 60 / 24 != 0) {
            return Math.abs(dateCreated / 1000 / 60 / 60 / 24) + " day(s) ago";
        } else if (dateCreated / 1000 / 60 / 60 != 0) {
            return Math.abs(dateCreated / 1000 / 60 / 60) + " hour(s) ago";
        } else if (dateCreated / 1000 / 60 != 0) {
            return Math.abs(dateCreated / 1000 / 60) + " minute(s) ago";
        } else {
            return Math.abs(dateCreated / 1000) + " second(s) ago";
        }
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public byte[] getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(byte[] secret_key) {
        this.secret_key = secret_key;
    }

    private String decryptMessage(String msg){
        CryptoDecryptInterface decryptor = new AdvancedEncryptionStandard(secret_key);
        return new String(decryptor.decrypt(msg.getBytes()));
    }

    private String encryptMessage(String msg){
        CryptoEncryptInterface encryptor = new AdvancedEncryptionStandard(secret_key);
        return new String(encryptor.encrypt(msg.getBytes()));
    }
}
