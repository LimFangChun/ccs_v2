package my.edu.tarc.communechat_v2.internal;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.Friendship;
import my.edu.tarc.communechat_v2.model.Message;
import my.edu.tarc.communechat_v2.model.Participant;
import my.edu.tarc.communechat_v2.model.Student;
import my.edu.tarc.communechat_v2.model.User;

/**
 * Created by Lim Fang Chun on 30-Sep-2018
 */

public class MqttHelper {

    private static final String TAG = "[MQTTHelper]";
    private MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    private DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
    private String clientId;
    //received results and headers
    private String receivedHeader;
    private String receivedResult;

    //change MQTT broker IP address here

    private static final String serverUri = "tcp://192.168.0.110:1883";//change to your broker's IP, window key+r -> cmd -> ipconfig
    //private static final String serverUri = "tcp://broker.hivemq.com:1883";
    private static final String mqttUsername = "leo477831@gmail.com";
    private static final String mqttPassword = "ba6acd07";

    private static int QoS = 1;
    private static final String topicPrefix = "/MY/TARUC/CCS/000000001/PUB/";
    //private static final String topicPrefix = "/leo477831@gmail.com/MY/TARUC/CCS/000000001/PUB/";
    private static boolean retain = false;
    private static boolean cleanSession = false;
    private static boolean automaticReconnect = true;

    public MqttHelper() {
        clientId = MqttClient.generateClientId();
        mqttConnectOptions.setUserName(mqttUsername);
        mqttConnectOptions.setPassword(mqttPassword.toCharArray());
    }

    public void connect(Context context) {
        if (mqttAndroidClient == null || !mqttAndroidClient.isConnected()) {
            mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
            try {
                IMqttToken token = mqttAndroidClient.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, mqttAndroidClient.getClientId() + " has connected to MQTT broker: " + serverUri);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i(TAG, mqttAndroidClient.getClientId() + " failed to connect. " + exception);
                    }
                });
                //mqttAndroidClient.connect().waitForCompletion();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void connectPublishSubscribe(Context context, final String topic, final String header, final Object data) {
        if (mqttAndroidClient == null || !mqttAndroidClient.isConnected()) {

            mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
            try {
                IMqttToken token = mqttAndroidClient.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        publish(topic, header, data);
                        subscribe(topic);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i(TAG, mqttAndroidClient.getClientId() + " failed to connect. " + exception);
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            publish(topic, header, data);
            subscribe(topic);
        }
    }

    public void connectPublish(Context context, final String topic, final String header, final Object data) {
        if (mqttAndroidClient == null || !mqttAndroidClient.isConnected()) {

            mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
            try {
                IMqttToken token = mqttAndroidClient.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        publish(topic, header, data);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i(TAG, mqttAndroidClient.getClientId() + " failed to connect. " + exception);
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            publish(topic, header, data);
        }
    }

    //done by 1st generation seniors
    //2nd generation has no idea what is this
    //will leave it here if you figure out how to use it
    private void setDisconnectBufferOption() {
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(true);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
    }

    public void subscribe(final String subscriptionTopic) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                IMqttToken subToken = mqttAndroidClient.subscribe(topicPrefix + subscriptionTopic, QoS);
                subToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, "Subscribed to " + topicPrefix + subscriptionTopic);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i(TAG, "Failed subscribe to " + topicPrefix + subscriptionTopic);
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void unsubscribe(final String topic) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                IMqttToken unsubToken = mqttAndroidClient.unsubscribe(topic);
                unsubToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, "Unsubscribe from " + topic);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i(TAG, "Failed to unsubscribe from " + topic);
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void publish(final String publishTopic, final String header, final Object data) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {

            byte[] encodedPayload = new byte[0];
            try {
                encodedPayload = encode(header, data).getBytes();
                MqttMessage message = new MqttMessage(encodedPayload);
                mqttAndroidClient.publish(topicPrefix + publishTopic, message);
                Log.i(TAG, "Published topic: " + topicPrefix + publishTopic);
                Log.i(TAG, "Published message: " + encode(header, data));
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void publish(final String publishTopic, final String header, final Object data, boolean retain) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            byte[] encodedPayload = new byte[0];
            try {
                encodedPayload = encode(header, data).getBytes();
                MqttMessage message = new MqttMessage(encodedPayload);
                message.setRetained(retain);
                mqttAndroidClient.publish(topicPrefix + publishTopic, message);
                Log.i(TAG, "Published topic: " + topicPrefix + publishTopic);
                Log.i(TAG, "Published message: " + encodedPayload);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                IMqttToken disconnectToken = mqttAndroidClient.disconnect();
                disconnectToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, "Client has disconnected from broker: " + mqttAndroidClient.getClientId());
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i(TAG, "Client failed to disconnect from broker: " + mqttAndroidClient.getClientId());
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    //encode the message to be published
    //later at server side will be using split method to decode message
    private String encode(String header, Object data) {
        StringBuilder temp = new StringBuilder();
        String result = null;
        switch (header) {
            case MqttHeader.LOGIN: {
                User loginUser = (User) data;
                temp.append(MqttHeader.LOGIN)
                        .append(",")
                        .append(loginUser.getUsername())
                        .append(",")
                        .append(loginUser.getPassword());
                result = temp.toString();
                break;
            }
            case MqttHeader.REGISTER_USER: {
                User newUser = (User) data;
                temp.append(MqttHeader.REGISTER_USER)
                        .append(",")
                        .append(newUser.getUsername())
                        .append(",")
                        .append(newUser.getPassword());
                result = temp.toString();
                break;
            }
            case MqttHeader.UPDATE_USER_STATUS: {
                User user = (User) data;
                temp.append(MqttHeader.UPDATE_USER_STATUS)
                        .append(",")
                        .append(user.getUser_id())
                        .append(",")
                        .append(user.getStatus());
                result = temp.toString();
                break;
            }
            case MqttHeader.COUNT_FRIEND_REQUEST: {
                User newUser = (User) data;
                temp.append(MqttHeader.COUNT_FRIEND_REQUEST)
                        .append(",")
                        .append(newUser.getUser_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.GET_CHAT_ROOM: {
                User user = (User) data;
                temp.append(MqttHeader.GET_CHAT_ROOM)
                        .append(",")
                        .append(user.getUser_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.GET_ROOM_MESSAGE: {
                Chat_Room chatRoom = (Chat_Room) data;
                temp.append(MqttHeader.GET_ROOM_MESSAGE)
                        .append(",")
                        .append(chatRoom.getRoom_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.SEND_ROOM_MESSAGE: {
                Message message = (Message) data;
                JSONObject messageJSON = new JSONObject();
                try {
                    messageJSON.put(Message.COL_ROOM_ID, String.valueOf(message.getRoom_id()));
                    messageJSON.put(Message.COL_SENDER_ID, String.valueOf(message.getSender_id()));
                    messageJSON.put(Message.COL_MESSAGE_TYPE, message.getMessage_type());
                    messageJSON.put(Message.COL_MESSAGE, message.getMessage());
                    messageJSON.put(Message.COL_DATE_CREATED, message.getDate_created().toString());
                    messageJSON.put(Message.COL_SENDER_NAME, message.getSender_name());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                temp.append(MqttHeader.SEND_ROOM_MESSAGE)
                        .append(",")
                        .append(messageJSON.toString());
                result = temp.toString();
                break;
            }
            case MqttHeader.CREATE_CHAT_ROOM: {
                Chat_Room chatRoom = (Chat_Room) data;
                temp.append(MqttHeader.CREATE_CHAT_ROOM)
                        .append(",")
                        .append(chatRoom.getOwner_id())
                        .append(",")
                        .append(chatRoom.getRoom_name());
                result = temp.toString();
                break;
            }
            case MqttHeader.ADD_PEOPLE_TO_GROUP: {
                Participant participant = (Participant) data;
                temp.append(MqttHeader.ADD_PEOPLE_TO_GROUP)
                        .append(",")
                        .append(participant.getRoom_id())
                        .append(",")
                        .append(participant.getUser_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.REMOVE_PEOPLE_FROM_GROUP: {
                Participant participant = (Participant) data;
                temp.append(MqttHeader.REMOVE_PEOPLE_FROM_GROUP)
                        .append(",")
                        .append(participant.getRoom_id())
                        .append(",")
                        .append(participant.getUser_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.GET_FRIEND_LIST_FOR_PARTICIPANT_ADD: {
                Participant participant = (Participant) data;
                temp.append(MqttHeader.GET_FRIEND_LIST_FOR_PARTICIPANT_ADD)
                        .append(",")
                        .append(participant.getRoom_id())
                        .append(",")
                        .append(participant.getUser_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.GET_PARTICIPANT_LIST_REMOVE: {
                Participant participant = (Participant) data;
                temp.append(MqttHeader.GET_PARTICIPANT_LIST_REMOVE)
                        .append(",")
                        .append(participant.getRoom_id())
                        .append(",")
                        .append(participant.getUser_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.DELETE_CHAT_ROOM: {
                Participant participant = (Participant) data;
                temp.append(MqttHeader.DELETE_CHAT_ROOM)
                        .append(",")
                        .append(participant.getRoom_id())
                        .append(",")
                        .append(participant.getUser_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.GET_ROOM_INFO: {
                Chat_Room room = (Chat_Room) data;
                temp.append(MqttHeader.GET_ROOM_INFO)
                        .append(",")
                        .append(room.getRoom_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.GET_FRIEND_REQUEST: {
                User newUser = (User) data;
                temp.append(MqttHeader.GET_FRIEND_REQUEST)
                        .append(",")
                        .append(newUser.getUser_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.GET_FRIEND_LIST: {
                User user = (User) data;
                temp.append(MqttHeader.GET_FRIEND_LIST)
                        .append(",")
                        .append(user.getUser_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.FIND_BY_ADDRESS: {
                User user = (User) data;
                temp.append(MqttHeader.FIND_BY_ADDRESS)
                        .append(",")
                        .append(user.getUser_id())
                        .append(",")
                        .append(user.getCity_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.FIND_BY_PROGRAMME: {
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
            case MqttHeader.FIND_BY_TUTORIAL_GROUP: {
                Student student = (Student) data;
                temp.append(MqttHeader.FIND_BY_TUTORIAL_GROUP)
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
            case MqttHeader.FIND_BY_AGE: {
                Student student = (Student) data;
                temp.append(MqttHeader.FIND_BY_AGE)
                        .append(",")
                        .append(student.getUser_id())
                        .append(",")
                        .append(student.getNric().substring(0, 2))
                        .append(",")
                        .append(student.getFaculty())
                        .append(",")
                        .append(student.getCourse());
                result = temp.toString();
                break;
            }
            case MqttHeader.FIND_BY_LOCATION: {
                User user = (User) data;
                temp.append(MqttHeader.FIND_BY_LOCATION)
                        .append(",")
                        .append(user.getUser_id())
                        .append(",")
                        .append(user.getLast_longitude())
                        .append(",")
                        .append(user.getLast_latitude());
                result = temp.toString();
                break;
            }
            case MqttHeader.UPDATE_LOCATION: {
                User user = (User) data;
                temp.append(MqttHeader.UPDATE_LOCATION)
                        .append(",")
                        .append(user.getUser_id())
                        .append(",")
                        .append(user.getLast_longitude())
                        .append(",")
                        .append(user.getLast_latitude());
                result = temp.toString();
                break;
            }
            case MqttHeader.REQ_ADD_FRIEND: {
                Friendship friendship = (Friendship) data;
                temp.append(MqttHeader.REQ_ADD_FRIEND)
                        .append(",")
                        .append(friendship.getUser_id())
                        .append(",")
                        .append(friendship.getFriend_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.ADD_FRIEND: {
                Friendship friendship = (Friendship) data;
                temp.append(MqttHeader.ADD_FRIEND)
                        .append(",")
                        .append(friendship.getUser_id())
                        .append(",")
                        .append(friendship.getFriend_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.DELETE_FRIEND: {
                Friendship friendship = (Friendship) data;
                temp.append(MqttHeader.DELETE_FRIEND)
                        .append(",")
                        .append(friendship.getUser_id())
                        .append(",")
                        .append(friendship.getFriend_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.SEARCH_USER: {
                User user = (User) data;
                temp.append(MqttHeader.SEARCH_USER)
                        .append(",")
                        .append(user.getUser_id())
                        .append(",")
                        .append(user.getUsername());
                result = temp.toString();
                break;
            }
            case MqttHeader.UPDATE_PUBLIC_KEY: {
                User user = (User) data;
                temp.append(MqttHeader.UPDATE_PUBLIC_KEY)
                        .append(",")
                        .append(user.getUser_id())
                        .append(",")
                        .append(user.getPublic_key());
                result = temp.toString();
                break;
            }
        }
        return result;
    }

    //decode the message received from MQTT broker
    //same as server side, use split method
    public void decode(String msg) {
        if (msg != null && !msg.isEmpty()) {
            receivedHeader = msg.split(",")[0];
            receivedResult = msg.split(",", 2)[1];
        } else {
            receivedHeader = MqttHeader.NO_REPLY;
            receivedResult = MqttHeader.NO_RESULT;
        }
    }

    public String getReceivedHeader() {
        return receivedHeader;
    }

    public String getReceivedResult() {
        return receivedResult;
    }

    public MqttAndroidClient getMqttClient() {
        return mqttAndroidClient;
    }
}
