package my.edu.tarc.communechat_v2.internal;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Date;
import java.util.UUID;

import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.KEEP_ALIVE_INTERVAL_DEFAULT;

/**
 * Created by Xeosz on 24-Sep-17.
 *
 * ***ATTENTION***
 *  MQTT will only callback to one of the MQTT Android Client with same ClientID
 *  Which is why static class is applied for whole application context.
 *  Do not create/start multiple MQTT connection / MqttAndroidClient
 */

public final class MqttHelper {
    private static final String TAG = "MQTTHelper";
    private static MqttAndroidClient mqttAndroidClient;
    private static MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    private static DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();

    //Static MQTT Connection Variables
    private static String userTopic;
    private static String clientId;

    //change MQTT broker IP address here
    private static String serverUri = "tcp://192.168.0.106:1883";//change to your broker's IP, window key+r -> cmd -> ipconfig
    private static String mqttUsername = "";
    private static String mqttPassword = "";
    
    private static int QoS = 1;
    private static String topicPrefix = "MY/TARUC/CCS/000000001/PUB/USER/";
    private static boolean retain = false;
    private static boolean cleanSession = false;
    private static boolean automaticReconnect = true;

    private static String generateRandomClientId() {
        String result;
        result = UUID.randomUUID().toString().substring(0, 8) + "-" + (new Date().getTime() / 1000);
        return result;
    }

    public static void startMqtt(Context context) {
        clientId = generateRandomClientId();
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);
        try {
            IMqttToken token = connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i("[MQTTHELPER]before", mqttAndroidClient.getClientId());

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.i("[MQTTHELPER]before", "fail");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public static void startMqtt(Context context, String Id) {
        userTopic = getUserTopic(Id);
        clientId = Id;
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);

//        if (mqttAndroidClient.isConnected()) {
//            try {
//                mqttAndroidClient.disconnect();
//            } catch (MqttException e) {
//                e.printStackTrace();
//            }
//        }

        if (!mqttAndroidClient.isConnected()){
            try {
                IMqttToken token = connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i("[MQTTHELPER]after", mqttAndroidClient.getClientId());
                        subscribe(MqttHelper.getSubscribeTopic());
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.i("[MQTTHELPER]after", "fail");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

    }

    public static String getSubscribeTopic() {
        return userTopic;
    }

    public static String getPublishTopic() {
        return userTopic;
    }

    private static String getUserTopic(String uid) {
        return topicPrefix + uid;
    }

    public static void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }

    private static void setDisconnectBufferOption() {
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(100);
        disconnectedBufferOptions.setPersistBuffer(true);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
    }

    private static IMqttToken connect() throws MqttException {
        mqttConnectOptions.setAutomaticReconnect(automaticReconnect);
        mqttConnectOptions.setCleanSession(cleanSession);
        //mqttConnectOptions.setUserName(mqttUsername);
        //mqttConnectOptions.setPassword(mqttPassword.toCharArray());
        mqttConnectOptions.setKeepAliveInterval(KEEP_ALIVE_INTERVAL_DEFAULT);
        return mqttAndroidClient.connect(mqttConnectOptions);
    }

    public static void subscribe(final String subscriptionTopic) {
        if (mqttAndroidClient != null && !mqttAndroidClient.isConnected()) {
            IMqttToken token;
            try {
                token = connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        setDisconnectBufferOption();
                        try {
                            mqttAndroidClient.subscribe(subscriptionTopic, QoS);
                            Log.i(TAG, "[SUBSCRIBED] " + subscriptionTopic);
                        } catch (MqttException e) {
                            Log.w(TAG, "Failed to subscribe to topic: " + subscriptionTopic + ".");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
            } catch (MqttException e) {
                Log.w(TAG, "Failed to connect and subscribe.");
                e.printStackTrace();
            }
        } else {
            try {
                setDisconnectBufferOption();
                mqttAndroidClient.subscribe(subscriptionTopic, QoS);
                Log.i(TAG, "[SUBSCRIBED] " + subscriptionTopic);
            } catch (MqttException e) {
                Log.w(TAG, "Failed to connect and subscribe.");
                e.printStackTrace();
            }
        }
    }

    public static void publish(final String publishTopic, final String payload) {
        final MqttMessage message = new MqttMessage();
        message.setPayload(payload.getBytes());
        message.setQos(QoS);
        message.setRetained(retain);
        if (!mqttAndroidClient.isConnected()) {
            IMqttToken token = null;
            try {
                token = connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        setDisconnectBufferOption();
                        try {
                            mqttAndroidClient.publish(publishTopic, message);
                            Log.i(TAG, "[PUBLISH] " + message);
                        } catch (MqttException e) {
                            Log.w(TAG, "Failed to publish messsage: \"" + payload + "\" on topic: " + publishTopic + ".");
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            try {
                setDisconnectBufferOption();
                mqttAndroidClient.publish(publishTopic, message);
            } catch (MqttException e) {
                Log.w(TAG, "Failed to publish messsage: \"" + payload + "\" on topic: " + publishTopic + ".");
                e.printStackTrace();
            }
        }
    }

    public static void unsubscribe(final String subscriptionTopic) {
        if (!mqttAndroidClient.isConnected()) {
            try {
                IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        try {
                            mqttAndroidClient.unsubscribe(subscriptionTopic, null, new IMqttActionListener() {
                                @Override
                                public void onSuccess(IMqttToken asyncActionToken) {
                                    Log.i(TAG, "Unsubscribeb to topic: " + subscriptionTopic + ".");
                                }

                                @Override
                                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                    Log.w(TAG, "Failed to unsubscribe topic: " + subscriptionTopic + ".");
                                }
                            });
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            try {
                mqttAndroidClient.unsubscribe(subscriptionTopic, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, "Unsubscribeb to topic: " + subscriptionTopic + ".");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.w(TAG, "Failed to unsubscribe topic: " + subscriptionTopic + ".");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public static void disconnect() {
        if (mqttAndroidClient.isConnected()) {
            try {
                IMqttToken token = mqttAndroidClient.disconnect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.i(TAG, "Disconnected to the server " + serverUri);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
