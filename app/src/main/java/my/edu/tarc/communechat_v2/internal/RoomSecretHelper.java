package my.edu.tarc.communechat_v2.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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


	public static void listenIncomingSecrets(final Context context, int userID) {
		//subscribe to my own topic
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
				if (mqttHelper.getReceivedHeader().equals(MqttHeader.CHATROOM_SECRET)) {
					SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
					JSONArray result = new JSONArray(mqttHelper.getReceivedResult());
					JSONObject temp = result.getJSONObject(0);
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
					RSA rsa = new RSA(pref.getString(User.COL_PRIVATE_KEY, null).getBytes(), RSA.RSA_CONSTRUCT_WITH_PRIVATE);
					editor.putString(getRoomPrefKey(temp.getInt(Chat_Room.COL_ROOM_ID)), new String(rsa.decrypt(temp.getString(Chat_Room.COL_SECRET_KEY).getBytes())));
					editor.commit();
				}
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {

			}
		});
	}

	public static void sendRoomSecret(final Context context, final User[] users, final Chat_Room chat_room) {
		String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);

		for (final User user : users) {
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
						//encrypt aes with public
						//publish to others' topic
						SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
						RSA rsa = new RSA(pref.getString(User.COL_PRIVATE_KEY, null).getBytes(), RSA.RSA_CONSTRUCT_WITH_PRIVATE);
						mqttHelper.connectPublish(context, userTopic(user.getUser_id()), MqttHeader.CHATROOM_SECRET, rsa.encrypt(chat_room.getSecret_key()));
						mqttHelper.unsubscribe(topic);
					}
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {

				}
			});
		}
	}

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
					//encrypt aes with public
					//publish to others' topic
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
					RSA rsa = new RSA(pref.getString(User.COL_PRIVATE_KEY, null).getBytes(), RSA.RSA_CONSTRUCT_WITH_PRIVATE);
					Chat_Room chat_room1 = new Chat_Room();
					chat_room1.setSecret_key(rsa.encrypt(chat_room.getSecret_key()));
					mqttHelper.connectPublish(context, userTopic(user.getUser_id()), MqttHeader.CHATROOM_SECRET, chat_room1);
					mqttHelper.unsubscribe(topic);
				}
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {

			}
		});
	}

	private static String userTopic(int userID) {
		return "roomSecret/" + Integer.toString(userID);
	}

	public static String getRoomPrefKey(int room_id){
		return Chat_Room.COL_SECRET_KEY.concat("/chatroom".concat(Integer.toString(room_id)));
	}
}
