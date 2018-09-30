package my.edu.tarc.communechat_v2.internal;

import java.util.ArrayList;

import my.edu.tarc.communechat_v2.model.Contact;
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
 */

public class MqttMessageHandler {
    private final static String RESERVED_STRING = "000000000000000000000000";

    //Mqtt Command String
    private static String REQ_AUTHENTICATION = "003801";
    private static String ACK_AUTHENTICATION = "003802";
    private static String REQ_CONTACT_LIST = "003810";
    private static String ACK_CONTACT_LIST = "003811";
    private static String REQ_CONTACT_DETAILS = "003812";
    private static String ACK_CONTACT_DETAILS = "003813";
    private static String REQ_NEARBY_FRIENDS = "003814";
    private static String ACK_NEARBY_FRIENDS = "003815";
    private static String REQ_SEARCH_USER = "003816";
    private static String ACK_SEARCH_USER = "003817";
    private static String REQ_RECOMMEND_FRIENDS = "003818";
    private static String ACK_RECOMMEND_FRIENDS = "003819";
    private static String REQ_FRIEND_REQUEST = "003820";
    private static String ACK_FRIEND_REQUEST = "003821";
    private static String REQ_FRIEND_REQUEST_LIST = "003822";
    private static String ACK_FRIEND_REQUEST_LIST = "003823";
    private static String REQ_RESPONSE_FRIEND_REQUEST = "003824";
    private static String ACK_RESPONSE_FRIEND_REQUEST = "003825";

    // Progressive command for searching user by category
    // retrieve available records from database
    // 003826 to 003837
    private static String REQ_SEARCH_CATEGORY_FACULTY = "003826";
    private static String ACK_SEARCH_CATEGORY_FACULTY = "003827";
    private static String REQ_SEARCH_CATEGORY_YEAR = "003828";
    private static String ACK_SEARCH_CATEGORY_YEAR = "003829";
    private static String REQ_SEARCH_CATEGORY_SESSION = "003830";
    private static String ACK_SEARCH_CATEGORY_SESSION = "003831";
    private static String REQ_SEARCH_CATEGORY_COURSES = "003832";
    private static String ACK_SEARCH_CATEGORY_COURSES = "003833";
    private static String REQ_SEARCH_CATEGORY_GROUP = "003834";
    private static String ACK_SEARCH_CATEGORY_GROUP = "003835";
    private static String REQ_SEARCH_CATEGORY_MEMBER = "003836";
    private static String ACK_SEARCH_CATEGORY_MEMBER = "003837";

    private static String KEEP_ALIVE = "003999";

    public MqttCommand mqttCommand;
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
    public void encode(MqttCommand command, Object data) {
        StringBuilder sb = new StringBuilder();
        String result = null;
        switch (command) {
            case REQ_AUTHENTICATION: {
                String[] credential = (String[]) data;
                sb.append(REQ_AUTHENTICATION
                        + RESERVED_STRING
                        + String.format("%03d", credential[0].length())
                        + credential[0]
                        + String.format("%03d", credential[1].length())
                        + credential[1]);
                result = sb.toString();
                break;
            }
            case REQ_CONTACT_LIST: {
                String uid;
                uid = (String) data;
                sb.append(REQ_CONTACT_LIST
                        + RESERVED_STRING
                        + uid);
                result = sb.toString();
                break;
            }
            case REQ_CONTACT_DETAILS: {
                String uid;
                uid = (String) data;
                sb.append(REQ_CONTACT_DETAILS
                        + RESERVED_STRING
                        + uid);
                result = sb.toString();
                break;
            }
            case REQ_NEARBY_FRIENDS: {
                String uid = (String) data;
                sb.append(REQ_NEARBY_FRIENDS
                        + RESERVED_STRING
                        + uid);
                result = sb.toString();
                break;
            }
            case REQ_SEARCH_USER: {
                String searchString = (String) data;
                sb.append(REQ_SEARCH_USER
                        + RESERVED_STRING
                        + searchString);
                result = sb.toString();
                break;
            }
            case REQ_RECOMMEND_FRIENDS: {
                String uid = (String) data;
                sb.append(REQ_RECOMMEND_FRIENDS
                        + RESERVED_STRING
                        + uid);
                result = sb.toString();
                break;
            }
            case REQ_FRIEND_REQUEST: {
                String[] str = (String[]) data;
                sb.append(REQ_FRIEND_REQUEST
                        + RESERVED_STRING
                        + str[0]
                        + str[1]
                        + str[2]);
                result = sb.toString();
                break;
            }
            case REQ_FRIEND_REQUEST_LIST: {
                String uid = (String) data;
                sb.append(REQ_FRIEND_REQUEST_LIST
                        + RESERVED_STRING
                        + uid);
                result = sb.toString();
                break;
            }
            case REQ_RESPONSE_FRIEND_REQUEST: {
                String[] id = (String[]) data;
                sb.append(REQ_RESPONSE_FRIEND_REQUEST
                        + RESERVED_STRING
                        + id[0]
                        + id[1]);
                result = sb.toString();
                break;
            }
            case REQ_SEARCH_CATEGORY_FACULTY: {
                sb.append(REQ_SEARCH_CATEGORY_FACULTY
                        + RESERVED_STRING);
                result = sb.toString();
                break;
            }
            case REQ_SEARCH_CATEGORY_YEAR: {
                String[] str = (String[]) data;
                sb.append(REQ_SEARCH_CATEGORY_YEAR
                        + RESERVED_STRING
                        + str[0]);
                result = sb.toString();
                break;
            }
            case REQ_SEARCH_CATEGORY_SESSION: {
                String[] strings = (String[]) data;
                sb.append(REQ_SEARCH_CATEGORY_SESSION
                        + RESERVED_STRING
                        + strings[0]
                        + strings[1]);
                result = sb.toString();
                break;
            }
            case REQ_SEARCH_CATEGORY_COURSES: {
                String[] strings = (String[]) data;
                sb.append(REQ_SEARCH_CATEGORY_COURSES
                        + RESERVED_STRING
                        + strings[0]
                        + strings[1]
                        + strings[2]);
                result = sb.toString();
                break;
            }
            case REQ_SEARCH_CATEGORY_GROUP: {
                String[] strings = (String[]) data;
                sb.append(REQ_SEARCH_CATEGORY_GROUP
                        + RESERVED_STRING
                        + strings[0]
                        + strings[1]
                        + strings[2]
                        + strings[3]);
                result = sb.toString();
                break;
            }
            case REQ_SEARCH_CATEGORY_MEMBER: {
                String[] strings = (String[]) data;
                sb.append(REQ_SEARCH_CATEGORY_MEMBER
                        + RESERVED_STRING
                        + strings[0]
                        + strings[1]
                        + strings[2]
                        + strings[3]
                        + strings[4]);
                result = sb.toString();
                break;
            }
            case KEEP_ALIVE: {
                String[] update = (String[]) data;
                sb.append(KEEP_ALIVE
                        + RESERVED_STRING
                        + update[0]
                        + update[1]
                        + String.format("%02d", update[2].length())
                        + update[2]
                        + String.format("%02d", update[3].length())
                        + update[3]);
                result = sb.toString();
                break;
            }
        }
        this.publish = result;
    }

    //Decode the received Mqtt Message to check command
    private void decode(String msg) {
        if (!msg.isEmpty() && msg.length() >= 30) {
            String data = msg.substring(0, 6);

            if (data.equalsIgnoreCase(ACK_AUTHENTICATION)) {
                this.mqttCommand = MqttCommand.ACK_AUTHENTICATION;

            } else if (data.equalsIgnoreCase(ACK_CONTACT_LIST)) {
                this.mqttCommand = MqttCommand.ACK_CONTACT_LIST;

            } else if (data.equalsIgnoreCase(ACK_CONTACT_DETAILS)) {
                this.mqttCommand = MqttCommand.ACK_CONTACT_DETAILS;

            } else if (data.equalsIgnoreCase(ACK_NEARBY_FRIENDS)) {
                this.mqttCommand = MqttCommand.ACK_NEARBY_FRIENDS;

            } else if (data.equalsIgnoreCase(ACK_SEARCH_USER)) {
                this.mqttCommand = MqttCommand.ACK_SEARCH_USER;

            } else if (data.equalsIgnoreCase(ACK_RECOMMEND_FRIENDS)) {
                this.mqttCommand = MqttCommand.ACK_RECOMMEND_FRIENDS;

            } else if (data.equalsIgnoreCase(ACK_FRIEND_REQUEST)) {
                this.mqttCommand = MqttCommand.ACK_FRIEND_REQUEST;

            } else if (data.equalsIgnoreCase(ACK_FRIEND_REQUEST_LIST)) {
                this.mqttCommand = MqttCommand.ACK_FRIEND_REQUEST_LIST;

            } else if (data.equalsIgnoreCase(ACK_RESPONSE_FRIEND_REQUEST)) {
                this.mqttCommand = MqttCommand.ACK_RESPONSE_FRIEND_REQUEST;

            } else if (data.equalsIgnoreCase(ACK_SEARCH_CATEGORY_FACULTY)) {
                this.mqttCommand = MqttCommand.ACK_SEARCH_CATEGORY_FACULTY;

            } else if (data.equalsIgnoreCase(ACK_SEARCH_CATEGORY_YEAR)) {
                this.mqttCommand = MqttCommand.ACK_SEARCH_CATEGORY_YEAR;

            } else if (data.equalsIgnoreCase(ACK_SEARCH_CATEGORY_SESSION)) {
                this.mqttCommand = MqttCommand.ACK_SEARCH_CATEGORY_SESSION;

            } else if (data.equalsIgnoreCase(ACK_SEARCH_CATEGORY_COURSES)) {
                this.mqttCommand = MqttCommand.ACK_SEARCH_CATEGORY_COURSES;

            } else if (data.equalsIgnoreCase(ACK_SEARCH_CATEGORY_GROUP)) {
                this.mqttCommand = MqttCommand.ACK_SEARCH_CATEGORY_GROUP;

            } else if (data.equalsIgnoreCase(ACK_SEARCH_CATEGORY_MEMBER)) {
                this.mqttCommand = MqttCommand.ACK_SEARCH_CATEGORY_MEMBER;

            } else
                this.mqttCommand = null;
        }
    }

    //------All functions below are for handling received Mqtt Messages------
    //      Convert the message strings to desired data.
    public int isLoginAuthenticated() {
        if (this.mqttCommand == MqttCommand.ACK_AUTHENTICATION) {
            String data = received.substring(30);
            if (data.equalsIgnoreCase("1")) {
                return 1;
            } else if (data.equalsIgnoreCase("2")) {
                return 2;
            } else
                return 3;
        } else
            return 4;
    }

    public User getUserData() {
        User user = new User();
        if (this.mqttCommand == MqttCommand.ACK_AUTHENTICATION) {
            received = received.substring(30);
            int temp = 0;
            String data = received;

            user.setStudent_id(data.substring(0, 10));
            data = data.substring(10);

            user.setFaculty(data.substring(0, 4));
            data = data.substring(4);

            user.setCourse(data.substring(0, 3));
            data = data.substring(3);

            user.setTutorial_group(Integer.parseInt(data.substring(0, 2)));
            data = data.substring(2);

            user.setIntake(data.substring(0, 6));
            data = data.substring(6);

            user.setAcademic_year(Integer.parseInt(data.substring(0, 4)));
            data = data.substring(4);

            user.setUid(Integer.parseInt(data.substring(0, 10)));
            data = data.substring(10);

            user.setGender(Integer.parseInt(data.substring(0, 1)));
            data = data.substring(1);

            user.setBirth_year(Integer.parseInt(data.substring(0, 4)));
            data = data.substring(4);

            user.setBirth_month(Integer.parseInt(data.substring(0, 2)));
            data = data.substring(2);

            user.setBirth_day(Integer.parseInt(data.substring(0, 2)));
            data = data.substring(2);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            user.setUsername(data.substring(0, temp));
            data = data.substring(temp);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            user.setNickname(data.substring(0, temp));
            data = data.substring(temp);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            user.setPassword(data.substring(0, temp));
            data = data.substring(temp);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            user.setStatus(data.substring(0, temp));
            data = data.substring(temp);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            user.setPhone_number(data.substring(0, temp));
            data = data.substring(temp);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            user.setEmail(data.substring(0, temp));
            data = data.substring(temp);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            user.setAddress(data.substring(0, temp));
            data = data.substring(temp);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            user.setTown(data.substring(0, temp));
            data = data.substring(temp);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            user.setState(data.substring(0, temp));
            data = data.substring(temp);

            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            user.setPostal_code(data.substring(0, temp));
            data = data.substring(temp);


            temp = Integer.parseInt(data.substring(0, 3));
            data = data.substring(3);
            user.setCountry(data.substring(0, temp));

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
