package my.edu.tarc.communechat_v2.internal;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import my.edu.tarc.communechat_v2.model.Contact;
import my.edu.tarc.communechat_v2.model.Friendship;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

/**
 * Created by Xeosz on 27-Sep-17.
 * SC-Commune Chat System
 * Command Range 003801 - 004000 + 24 reserved characters.
 *
 *      When adding a new command, specify the command string
 *      Add the command name to MQTTCommand Enum
 *      Update encode() for input to upload to server and design your handler for input data
 *      Update decode() and isReceiving() for handling received message
 *
 *
 * Update from 2nd generation seniors:
 * these things are done by 1st generation
 * we have no idea what are these
 * and, to be honest, its very crappy
 * we will leave it here
 * maybe a talented one can find any value in these
 */

public class MqttMessageHandler {
    public MqttCommand mqttCommand;//TODO: remove this
    private String receivedHeader;
    private String receivedResult;
    private String publish;
    private String received;

    public MqttMessageHandler() {
    }

    public String getPublish() {
        return publish;
    }

    public void setReceived(String received) {
        this.received = received;
        this.decode(received);
    }

    //After encode with MqttCommand and designed input data
    //Get the publish messages from getPublish() to publish the messages
    public void encode(String command, Object data) {
        StringBuilder temp = new StringBuilder();
        String result = null;
        switch (command) {
            case MqttHeader.LOGIN: {
                User loginUser = (User)data;
                temp.append(MqttHeader.LOGIN)
                        .append(",")
                        .append(loginUser.getUsername())
                        .append(",")
                        .append(loginUser.getPassword());
                result = temp.toString();
                break;
            }
            case MqttHeader.REGISTER_USER:{
                User newUser = (User)data;
                temp.append(MqttHeader.REGISTER_USER)
                        .append(",")
                        .append(newUser.getUsername())
                        .append(",")
                        .append(newUser.getPassword());
                result = temp.toString();
                break;
            }
            case MqttHeader.GET_FRIEND_LIST:{
                User user = (User)data;
                temp.append(MqttHeader.GET_FRIEND_LIST)
                        .append(",")
                        .append(user.getUser_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.FIND_BY_ADDRESS:{
                User user = (User)data;
                temp.append(MqttHeader.FIND_BY_ADDRESS)
                        .append(",")
                        .append(user.getUser_id())
                        .append(",")
                        .append(user.getCity_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.FIND_BY_PROGRAMME:{
                Student student = (Student) data;
                temp.append(MqttHeader.FIND_BY_PROGRAMME)
                        .append(",")
                        .append(student.getUser_id())
                        .append(",")
                        .append(student.getFaculty())
                        .append(",")
                        .append(student.getCourse())
                        .append(",")
                        .append(student.getTutorial_group());
                result = temp.toString();
                break;
            }
            case MqttHeader.FIND_BY_TUTORIAL_GROUP:{
                Student student = (Student) data;
                temp.append(MqttHeader.FIND_BY_PROGRAMME)
                        .append(",")
                        .append(student.getUser_id())
                        .append(",")
                        .append(student.getFaculty())
                        .append(",")
                        .append(student.getCourse())
                        .append(",")
                        .append(student.getTutorial_group())
                        .append(",")
                        .append(student.getIntake())
                        .append(",")
                        .append(student.getAcademic_year());
                result = temp.toString();
                break;
            }
            case MqttHeader.FIND_BY_AGE:{
                Student student = (Student) data;
                temp.append(MqttHeader.FIND_BY_AGE)
                        .append(",")
                        .append(student.getUser_id())
                        .append(",")
                        .append(student.getNric().substring(0,1))
                        .append(",")
                        .append(student.getFaculty())
                        .append(",")
                        .append(student.getCourse());
                result = temp.toString();
                break;
            }
            case MqttHeader.REQ_ADD_FRIEND:{
                Friendship friendship = (Friendship) data;
                temp.append(MqttHeader.REQ_ADD_FRIEND)
                        .append(",")
                        .append(friendship.getUser_id())
                        .append(",")
                        .append(friendship.getFriend_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.ADD_FRIEND:{
                Friendship friendship = (Friendship) data;
                temp.append(MqttHeader.ADD_FRIEND)
                        .append(",")
                        .append(friendship.getUser_id())
                        .append(",")
                        .append(friendship.getFriend_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.DELETE_FRIEND:{
                Friendship friendship = (Friendship) data;
                temp.append(MqttHeader.DELETE_FRIEND)
                        .append(",")
                        .append(friendship.getUser_id())
                        .append(",")
                        .append(friendship.getFriend_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.SEARCH_USER:{
                User user = (User) data;
                temp.append(MqttHeader.SEARCH_USER)
                        .append(",")
                        .append(user.getUser_id())
                        .append(",")
                        .append(user.getUsername());
                result = temp.toString();
                break;
            }
        }
        this.publish = result;
    }

    //Decode the received Mqtt Message
    //so the data can be processed later
    private void decode(String msg) {
        if (msg != null && !msg.isEmpty()) {
            receivedHeader = msg.split(",")[0];
            receivedResult = msg.split(",")[1];
        }else{
            receivedHeader = MqttHeader.NO_REPLY;
            receivedResult = "";
        }
    }

    //------All functions below are for handling received Mqtt Messages------
    //      Convert the message strings to desired data.
    public boolean isLoginAuthenticated() {
        return (this.receivedHeader.equals(MqttHeader.LOGIN_REPLY) &&
                !receivedResult.equalsIgnoreCase(MqttHeader.NO_RESULT));
    }

    @SuppressLint("NewApi")
    public User getUserData() {
        User user = new User();
        if (this.receivedHeader.equals(MqttHeader.LOGIN_REPLY)) {
            try {
                JSONArray userData = new JSONArray(receivedResult);
                JSONObject temp = userData.getJSONObject(0);
                user.setUser_id(temp.getInt(User.COL_USER_ID));
                user.setUsername(temp.getString(User.COL_USERNAME));
                user.setPassword(temp.getString(User.COL_PASSWORD));
                user.setPosition(temp.getString(User.COL_POSITION));
                user.setGender(temp.getString(User.COL_GENDER));
                user.setNric(temp.getString(User.COL_NRIC));
                user.setPhone_number(temp.getString(User.COL_PHONE_NUMBER));
                user.setEmail(temp.getString(User.COL_EMAIL));
                user.setAddress(temp.getString(User.COL_ADDRESS));
                user.setCity_id(temp.getString(User.COL_CITY_ID));
                user.setStatus(temp.getString(User.COL_STATUS));
                user.setLast_online(temp.getString(User.COL_LAST_ONLINE));
            } catch (JSONException|NullPointerException e) {
                e.printStackTrace();
            }
        }else{
            user = null;
        }
        return user;
    }

    public ArrayList<Contact> getContactList() {
        ArrayList<Contact> contacts = new ArrayList<>();
        if (this.mqttCommand == MqttCommand.ACK_CONTACT_LIST) {
            received = received.substring(30);
            int temp = 0;
            String data = received;
            // id  / sizeof / nickname / sizeof / status
            while (!data.isEmpty()) {
                Contact contact = new Contact();

                contact.setUid(Integer.parseInt(data.substring(0, 10)));
                data = data.substring(10);

                temp = Integer.parseInt(data.substring(0, 3));
                data = data.substring(3);
                contact.setNickname(data.substring(0, temp));
                data = data.substring(temp);

                temp = Integer.parseInt(data.substring(0, 3));
                data = data.substring(3);
                contact.setStatus(data.substring(0, temp));
                data = data.substring(temp);

                contacts.add(contact);
            }
        }
        return contacts;
    }

    public Contact getContactDetails() {
        Contact contact = new Contact();
        if (this.mqttCommand == MqttCommand.ACK_CONTACT_DETAILS) {
            received = received.substring(30);
            int temp;
            String data = received;

            contact.setUid(Integer.parseInt(data.substring(0, 10)));
            data = data.substring(10);

            contact.setGender(Integer.parseInt(data.substring(0, 1)));
            data = data.substring(1);

            contact.setLast_online(Integer.parseInt(data.substring(0, 10)));
            data = data.substring(10);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            contact.setUsername(data.substring(0, temp));
            data = data.substring(temp);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            contact.setNickname(data.substring(0, temp));
            data = data.substring(temp);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            contact.setStatus(data.substring(0, temp));
        }
        return contact;
    }

    public ArrayList<Contact> getNearbyFriends() {
        ArrayList<Contact> contacts = new ArrayList<>();
        if (mqttCommand == MqttCommand.ACK_NEARBY_FRIENDS) {
            received = received.substring(30);
            int temp;
            String data = received;
            while (!data.isEmpty()) {
                Contact contact = new Contact();

                contact.setUid(Integer.parseInt(data.substring(0, 10)));
                data = data.substring(10);

                temp = Integer.parseInt(data.substring(0, 3));
                data = data.substring(3);
                contact.setNickname(data.substring(0, temp));
                data = data.substring(temp);

                temp = Integer.parseInt(data.substring(0, 1));
                data = data.substring(1);
                contact.setDistance(Integer.parseInt(data.substring(0, temp)));
                data = data.substring(temp);

                contacts.add(contact);
            }
        }
        return contacts;
    }

    public ArrayList<Contact> getSearchResultByName() {
        ArrayList<Contact> contacts = new ArrayList<>();
        if (mqttCommand == MqttCommand.ACK_SEARCH_USER) {
            received = received.substring(30);
            int temp;
            String data = received;
            while (!data.isEmpty()) {
                Contact contact = new Contact();

                contact.setUid(Integer.parseInt(data.substring(0, 10)));
                data = data.substring(10);

                contact.setGender(Integer.parseInt(data.substring(0, 1)));
                data = data.substring(1);

                contact.setLast_online(Integer.parseInt(data.substring(0, 10)));
                data = data.substring(10);

                temp = Integer.parseInt(data.substring(0, 3));
                data = data.substring(3);
                contact.setUsername(data.substring(0, temp));
                data = data.substring(temp);

                temp = Integer.parseInt(data.substring(0, 3));
                data = data.substring(3);
                contact.setNickname(data.substring(0, temp));
                data = data.substring(temp);

                temp = Integer.parseInt(data.substring(0, 3));
                data = data.substring(3);
                contact.setStatus(data.substring(0, temp));
                data = data.substring(temp);

                contacts.add(contact);
            }
        }
        return contacts;
    }

    public ArrayList<Contact> getRecommendedFriends() {
        ArrayList<Contact> contacts = new ArrayList<>();
        if (this.mqttCommand == MqttCommand.ACK_RECOMMEND_FRIENDS) {
            received = received.substring(30);
            int temp;
            String data = received;
            while (!data.isEmpty()) {
                Contact contact = new Contact();

                contact.setUid(Integer.parseInt(data.substring(0, 10)));
                data = data.substring(10);

                temp = Integer.parseInt(data.substring(0, 3));
                data = data.substring(3);
                contact.setNickname(data.substring(0, temp));
                data = data.substring(temp);

                temp = Integer.parseInt(data.substring(0, 1));
                data = data.substring(1);
                contact.setEdges(Integer.parseInt(data.substring(0, temp)));
                data = data.substring(temp);

                contacts.add(contact);
            }
        }
        return contacts;
    }

    public ArrayList<Contact> getFriendRequestList() {
        ArrayList<Contact> contacts = new ArrayList<>();
        if (this.mqttCommand == MqttCommand.ACK_FRIEND_REQUEST_LIST) {
            received = received.substring(30);
            int temp = 0;
            String data = received;

            while (!data.isEmpty()) {
                Contact contact = new Contact();

                contact.setUid(Integer.parseInt(data.substring(0, 10)));
                data = data.substring(10);

                temp = Integer.parseInt(data.substring(0, 3));
                data = data.substring(3);
                contact.setNickname(data.substring(0, temp));
                data = data.substring(temp);

                temp = Integer.parseInt(data.substring(0, 3));
                data = data.substring(3);
                contact.setStatus(data.substring(0, temp));
                data = data.substring(temp);

                contacts.add(contact);
            }
        }
        return contacts;
    }

    public ArrayList<String> getFaculties() {
        ArrayList<String> result = new ArrayList<String>();

        if (this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_FACULTY) {
            received = received.substring(30);
            String data = received;

            while (!data.isEmpty()) {
                result.add(data.substring(0, 4));
                data = data.substring(4);
            }
        }

        return result;
    }

    public ArrayList<String> getYears() {
        ArrayList<String> result = new ArrayList<String>();

        if (this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_YEAR) {
            received = received.substring(30);
            String data = received;

            while (!data.isEmpty()) {
                result.add(data.substring(0, 4));
                data = data.substring(4);
            }
        }

        return result;
    }

    public ArrayList<String> getSessions() {
        ArrayList<String> result = new ArrayList<String>();

        if (this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_SESSION) {
            received = received.substring(30);
            String data = received;

            while (!data.isEmpty()) {
                result.add(data.substring(0, 6));
                data = data.substring(6);
            }
        }

        return result;
    }

    public ArrayList<String> getCourses() {
        ArrayList<String> result = new ArrayList<String>();

        if (this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_COURSES) {
            received = received.substring(30);
            String data = received;

            while (!data.isEmpty()) {
                result.add(data.substring(0, 3));
                data = data.substring(3);
            }
        }

        return result;
    }

    public ArrayList<String> getGroups() {
        ArrayList<String> result = new ArrayList<String>();

        if (this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_GROUP) {
            received = received.substring(30);
            String data = received;

            while (!data.isEmpty()) {
                result.add(data.substring(0, 2));
                data = data.substring(2);
            }
        }

        return result;
    }

    public ArrayList<Contact> getStudents() {
        ArrayList<Contact> result = new ArrayList<Contact>();
        if (this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_MEMBER) {
            received = received.substring(30);
            String data = received;
            int temp;
            while (!data.isEmpty()) {
                Contact contact = new Contact();

                contact.setUid(Integer.parseInt(data.substring(0, 10)));
                data = data.substring(10);

                temp = Integer.parseInt(data.substring(0, 3));
                data = data.substring(3);
                contact.setNickname(data.substring(0, temp));
                data = data.substring(temp);

                result.add(contact);
            }
        }
        return result;
    }
    //--------------------------------------------------------------------------


    // If new command is define, please specify all incoming command enum.
    // This function is used in MessageService, anything please refer to the MessageService.
    protected boolean isReceiving() {
        return (this.mqttCommand == MqttCommand.ACK_AUTHENTICATION ||
                this.mqttCommand == MqttCommand.ACK_CONTACT_LIST ||
                this.mqttCommand == MqttCommand.ACK_SEARCH_USER ||
                this.mqttCommand == MqttCommand.ACK_CONTACT_DETAILS ||
                this.mqttCommand == MqttCommand.ACK_NEARBY_FRIENDS ||
                this.mqttCommand == MqttCommand.ACK_FRIEND_REQUEST ||
                this.mqttCommand == MqttCommand.ACK_FRIEND_REQUEST_LIST ||
                this.mqttCommand == MqttCommand.ACK_RECOMMEND_FRIENDS ||
                this.mqttCommand == MqttCommand.ACK_RESPONSE_FRIEND_REQUEST ||
                this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_FACULTY ||
                this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_YEAR ||
                this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_SESSION ||
                this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_COURSES ||
                this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_GROUP ||
                this.mqttCommand == MqttCommand.ACK_SEARCH_CATEGORY_MEMBER);
    }

    public String getReceivedHeader() {
        return receivedHeader;
    }

    public String getReceivedResult() {
        return receivedResult;
    }

    public void setReceivedHeader(String receivedHeader) {
        this.receivedHeader = receivedHeader;
    }

    public void setReceivedResult(String receivedResult) {
        this.receivedResult = receivedResult;
    }

    // New command must be defined as ENUM as shown below
    public enum MqttCommand {
        REQ_AUTHENTICATION,
        ACK_AUTHENTICATION,
        REQ_CONTACT_LIST,
        ACK_CONTACT_LIST,
        REQ_CONTACT_DETAILS,
        ACK_CONTACT_DETAILS,
        REQ_NEARBY_FRIENDS,
        ACK_NEARBY_FRIENDS,
        REQ_FRIEND_REQUEST,
        ACK_FRIEND_REQUEST,
        REQ_FRIEND_REQUEST_LIST,
        ACK_FRIEND_REQUEST_LIST,
        REQ_RESPONSE_FRIEND_REQUEST,
        ACK_RESPONSE_FRIEND_REQUEST,
        REQ_SEARCH_USER,
        ACK_SEARCH_USER,
        REQ_RECOMMEND_FRIENDS,
        ACK_RECOMMEND_FRIENDS,
        REQ_SEARCH_CATEGORY_FACULTY,
        ACK_SEARCH_CATEGORY_FACULTY,
        REQ_SEARCH_CATEGORY_YEAR,
        ACK_SEARCH_CATEGORY_YEAR,
        REQ_SEARCH_CATEGORY_SESSION,
        ACK_SEARCH_CATEGORY_SESSION,
        REQ_SEARCH_CATEGORY_COURSES,
        ACK_SEARCH_CATEGORY_COURSES,
        REQ_SEARCH_CATEGORY_GROUP,
        ACK_SEARCH_CATEGORY_GROUP,
        REQ_SEARCH_CATEGORY_MEMBER,
        ACK_SEARCH_CATEGORY_MEMBER,
        KEEP_ALIVE;
    }

}
