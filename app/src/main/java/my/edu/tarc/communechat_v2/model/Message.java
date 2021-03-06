package my.edu.tarc.communechat_v2.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity(foreignKeys = @ForeignKey(entity = Chat_Room.class, parentColumns = Chat_Room.COL_ROOM_ID, childColumns = Message.COL_ROOM_ID),
        tableName = "Message")
public class Message {
    //variables that define column name
    public static final String COL_MESSAGE_ID = "message_id";
    public static final String COL_MESSAGE = "message";
    public static final String COL_SENDER_ID = "sender_id";
    public static final String COL_DATE_CREATED = "date_created";
    public static final String COL_ROOM_ID = "room_id";
    public static final String COL_MESSAGE_TYPE = "message_type";
    public static final String COL_STATUS = "status";
    public static final String COL_SENDER_NAME = "sender_name";
    public static final String COL_MEDIA = "media";
    public static final String COL_IMAGE = "image";

    //variables for encapsulation
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_MESSAGE_ID)
    private int message_id;

    @ColumnInfo(name = COL_MESSAGE)
    private String message;

    @ColumnInfo(name = COL_SENDER_ID)
    private int sender_id;

    @ColumnInfo(name = COL_DATE_CREATED)
    private Calendar date_created;

    @ColumnInfo(name = COL_ROOM_ID)
    private int room_id;

    @ColumnInfo(name = COL_MESSAGE_TYPE)
    private String message_type;

    @ColumnInfo(name = COL_STATUS)
    private String status;

    @ColumnInfo(name = COL_SENDER_NAME)
    private String sender_name;

    @ColumnInfo(name = COL_MEDIA)
    private byte[] media;

    private String media64;

    private Uri mediaPath;

    public Message() {
        date_created = Calendar.getInstance();
    }

    public Message(int message_id, String message, int sender_id, String sender_name, int room_id) {
        this.message_id = message_id;
        this.message = message;
        this.sender_id = sender_id;
        this.sender_name = sender_name;
        this.room_id = room_id;
        date_created = Calendar.getInstance();
        message_type = "Text";
        status = "Unpinned";
    }

    public Message(int message_id, String message, int sender_id, Calendar date_created, int room_id, String message_type, String status) {
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

    public Calendar getDate_created() {
        return date_created;
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

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSender_name() {
        return sender_name;
    }

    public byte[] getMedia() {
        return media;
    }

    public void setMedia(byte[] media) {
        this.media = media;
    }

    public void setMedia(String media) {
        if (!media.isEmpty()) {
            this.media = media.getBytes();
        } else {
            this.media = null;
        }
    }

    public byte[] getByteArray(Context context, Uri uri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            assert inputStream != null;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Uri getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(Uri mediaPath) {
        this.mediaPath = mediaPath;
    }

    public Message copy() {
        Message message = new Message();
        message.sender_id = this.sender_id;
        message.date_created = this.date_created;
        message.message = this.message;
        message.room_id = this.room_id;
        message.message_type = this.message_type;
        message.sender_name = this.sender_name;
        return message;
    }

    public String getMedia64() {
        return media64;
    }

    public void setMedia64(String media64) {
        this.media64 = media64;
    }
}

