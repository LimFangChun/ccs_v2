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

import my.edu.tarc.communechat_v2.model.Friendship;
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
    private static final String serverUri = "tcp://192.168.0.19:1883";//change to your broker's IP, window key+r -> cmd -> ipconfig
    private static String mqttUsername = "";
    private static String mqttPassword = "";

    private static int QoS = 1;
    private static final String topicPrefix = "MY/TARUC/CCS/000000001/PUB/";
    private static boolean retain = false;
    private static boolean cleanSession = false;
    private static boolean automaticReconnect = true;

    public MqttHelper() {
        clientId = MqttClient.generateClientId();
    }

    public void connect(Context context) {
        if (mqttAndroidClient == null || !mqttAndroidClient.isConnected()) {

            mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
            try {
                IMqttToken token = mqttAndroidClient.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, mqttAndroidClient.getClientId() + " has connected to MQTT broker");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i(TAG, mqttAndroidClient.getClientId() + " failed to connect");
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
                        Log.i(TAG, mqttAndroidClient.getClientId() + " failed to connect");
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
                IMqttToken unsubToken = mqttAndroidClient.unsubscribe(topicPrefix + topic);
                unsubToken.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, "Unsubscribe from " + topicPrefix + topic);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i(TAG, "Failed to unsubscribe from " + topicPrefix + topic);
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
            case MqttHeader.COUNT_FRIEND_REQUEST: {
                User newUser = (User) data;
                temp.append(MqttHeader.COUNT_FRIEND_REQUEST)
                        .append(",")
                        .append(newUser.getUser_id());
                result = temp.toString();
                break;
            }
            case MqttHeader.GET_FRIEND_REQUEST:{
                User newUser = (User)data;
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
