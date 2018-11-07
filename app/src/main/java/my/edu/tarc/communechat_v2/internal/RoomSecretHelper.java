package my.edu.tarc.communechat_v2.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.RSA;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

public class RoomSecretHelper {


	private static final String TAG = "[RoomSecretHelper]";

	public static void listenIncomingSecrets(final Context context, int userID) {
		//subscribe to my own topic
		mqttHelper.connect(context);
		mqttHelper.subscribe(userTopic(userID));
		//listen to incoming secret key
		mqttHelper.getMqttClient().setCallback(new MqttCallback() {
			@Override
			public void connectionLost(Throwable cause) {

			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				mqttHelper.decode(message.toString());
				//decrypt and store
				if (mqttHelper.getReceivedHeader().equals(MqttHeader.CHATROOM_SECRET)
						&& !mqttHelper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
					SharedPreferences.Editor editor = pref.edit();
					//JSONArray result = new JSONArray(mqttHelper.getReceivedResult());
					JSONObject temp = new JSONObject(mqttHelper.getReceivedResult());
					RSA rsa = new RSA(pref.getString(User.COL_PRIVATE_KEY, null), RSA.RSA_CONSTRUCT_WITH_PRIVATE);
					editor.putString(getRoomPrefKey(temp.getInt(Chat_Room.COL_ROOM_ID)), rsa.decryptKey(temp.getString(Chat_Room.COL_SECRET_KEY)));
					editor.commit();
				}
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {

			}
		});
	}

	public static void sendRoomSecret(final Context context, final User[] users, final Chat_Room chat_room) {
		//use this when starting a group chat.
		for (final User user : users) {
			sendRoomSecret(context, user, chat_room);
		}
	}

	//Todo: use this when creating a chat room
	public static void sendRoomSecret(final Context context, final User user, final Chat_Room chat_room) {
		String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);

		//get public from server
		mqttHelper.connectPublishSubscribe(context, uniqueTopic, MqttHeader.GET_PUBLIC_KEY, user);
		mqttHelper.getMqttClient().setCallback(new MqttCallback() {
			@Override
			public void connectionLost(Throwable cause) {

			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				mqttHelper.decode(message.toString());
				if (mqttHelper.getReceivedHeader().equals(MqttHeader.GET_PUBLIC_KEY_REPLY) &&
						!mqttHelper.getReceivedResult().equals(MqttHeader.NO_RESULT)) {
					//encrypt aes key with public
					//publish to others' topic
					JSONArray result = new JSONArray(mqttHelper.getReceivedResult());
					JSONObject temp = result.getJSONObject(0);
					RSA rsa = new RSA(temp.getString(User.COL_PUBLIC_KEY), RSA.RSA_CONSTRUCT_WITH_PUBLIC);
					Chat_Room chat_room1 = new Chat_Room();
					chat_room1.setRoom_id(chat_room.getRoom_id());
					chat_room1.setSecret_key(rsa.encryptKey(chat_room.getSecret_key()));
					mqttHelper.unsubscribe(topic);
					mqttHelper.connectPublish(context, userTopic(user.getUser_id()), MqttHeader.CHATROOM_SECRET, chat_room1);
				}
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {

			}
		});
	}

	public static void requestKey(User me, Chat_Room chat_room){
		//Todo: request room key from owner
	}

	private static String userTopic(int userID) {
		return "roomSecret/".concat(Integer.toString(userID));
	}

	public static String getRoomPrefKey(int room_id){
		return Chat_Room.COL_SECRET_KEY.concat("/chatroom".concat(Integer.toString(room_id)));
	}
}
