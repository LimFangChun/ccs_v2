package my.edu.tarc.communechat_v2.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity(tableName = "Chat_Room")
public class Chat_Room {
    //variables that define column name
    public static final String COL_ROOM_ID = "room_id";
    public static final String COL_OWNER_ID = "owner_id";
    public static final String COL_ROOM_NAME = "room_name";
    public static final String COL_ROOM_TYPE = "room_type";
    public static final String COL_DATE_CREATED = "date_created";
    public static final String COL_LAST_UPDATE = "last_update";
    public static final String COL_TOPIC_ADDRESS = "topic_address";
    public static final String COL_SECRET_KEY = "secret_key";
    public static final String COL_PHOTO_URL = "photo_url";

    //variables for encapsulation
    @PrimaryKey
    @ColumnInfo(name = "room_id")
    private int room_id;

    @ColumnInfo(name = "owner_id")
    private int owner_id;

    @ColumnInfo(name = "room_name")
    private String room_name;

    @ColumnInfo(name = "date_created")
    private Calendar date_created;

    @ColumnInfo(name = "last_update")
    private Calendar last_update;

    @ColumnInfo(name = "topic_address")
    private String topic_address;

    @Ignore
    private String role;

    private String secret_key;

    private byte[] room_picture;

    private String room_type;

    public Chat_Room(){
        date_created = Calendar.getInstance();
        last_update = Calendar.getInstance();
    }

    public Chat_Room(int room_id, int owner_id, String room_name) {
        this.room_id = room_id;
        this.owner_id = owner_id;
        this.room_name = room_name;
        date_created = Calendar.getInstance();
        last_update = Calendar.getInstance();
        topic_address = "room/" + room_id;
        role = "";
    }

    public Chat_Room(int room_id, int owner_id, String room_name, Calendar date_created, Calendar last_update, String topic_address, String role) {
        this.room_id = room_id;
        this.owner_id = owner_id;
        this.room_name = room_name;
        this.date_created = date_created;
        this.last_update = last_update;
        this.topic_address = topic_address;
        this.role = role;
    }


    public Chat_Room(int room_id, int owner_id, String room_name, Calendar date_created, Calendar last_update, String topic_address, String role, String secret_key) {
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
        date_created = date_created.replace("T", " ");
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
        last_update = last_update.replace("T", " ");
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

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
    }

	public String decryptMessage(String msg){
		AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard(secret_key);
		return aes.decrypt(msg);
	}

	public String encryptMessage(String msg){
		AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard(secret_key);
		return aes.encrypt(msg);
	}

    public byte[] getRoom_picture() {
        return room_picture;
    }

    public void setRoom_picture(byte[] room_picture) {
        this.room_picture = room_picture;
    }

    public Bitmap getRoomPhotoBitmap() {
        return BitmapFactory.decodeByteArray(room_picture, 0, room_picture.length);
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }
}
