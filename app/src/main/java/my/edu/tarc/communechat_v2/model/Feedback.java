package my.edu.tarc.communechat_v2.model;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Feedback {
    public static final String FEEDBACK_ID = "FEEDBACK_ID";
    public static final String MESSAGE = "message";
    public static final String RATE = "rate";
    public static final String DATE_CREATED = "date_created";
    public static final String SENDER_ID = "user_id";

    private int feedback_id;
    private String message;
    private double rate;
    private Calendar date_created;
    private int sender_id;

    public Feedback() {
        date_created = Calendar.getInstance();
    }

    public int getFeedback_id() {
        return feedback_id;
    }

    public void setFeedback_id(int feedback_id) {
        this.feedback_id = feedback_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Calendar getDate_created() {
        return date_created;
    }

    public void setDate_created(Calendar date_created) {
        this.date_created = date_created;
    }

    public void setDate_created(String date_created) {
        date_created = date_created.replace("T", " ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        try {
            this.date_created.setTime(dateFormat.parse(date_created));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String toJSON() {
        try {
            JSONObject result = new JSONObject();
            result.put(MESSAGE, message);
            result.put(RATE, rate);
            result.put(SENDER_ID, sender_id);

            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
