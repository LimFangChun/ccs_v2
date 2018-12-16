package my.edu.tarc.communechat_v2.internal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.RSA;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;
import static my.edu.tarc.communechat_v2.internal.RoomSecretHelper.getOrGenerateRoomKey;
import static my.edu.tarc.communechat_v2.internal.RoomSecretHelper.getRoomPrefKey;

public class AsyncMqttMessageHandler extends AsyncTask<Object, Void, Void> {
	private static final String TAG = "[HandleMessageAsync]";
	@SuppressLint("StaticFieldLeak")
	private Context context;
	private MqttHelper mqttHelperTemp;
	private String header;
	private String result;

	public AsyncMqttMessageHandler(Context context, String message) {
		super();
		mqttHelperTemp = new MqttHelper(); //for decode message only.
		mqttHelperTemp.decode(message);
		this.context = context;
	}

	@Override
	protected Void doInBackground(Object... args) {
		header = mqttHelperTemp.getReceivedHeader();
		result = mqttHelperTemp.getReceivedResult();

		switch (header) {
			case MqttHeader.GET_CHATROOM_SECRET_ALL_REPLY: {
				if (!result.equals(MqttHeader.NO_RESULT)) {
					//decrypt and store
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
					SharedPreferences.Editor editor = pref.edit();
					try {
						//returns array of roomID and secretKey
						JSONArray resultArr = new JSONArray(result);
						//JSONObject temp = new JSONObject(result);
						for (int i = 0; i < resultArr.length(); i++) {
							JSONObject temp = resultArr.getJSONObject(i);
							RSA rsa = new RSA(pref.getString(User.COL_PRIVATE_KEY, null), RSA.RSA_CONSTRUCT_WITH_PRIVATE);
							editor.putString(getRoomPrefKey(temp.getInt(Chat_Room.COL_ROOM_ID)), rsa.decryptKey(temp.getString(Chat_Room.COL_SECRET_KEY)));
							Log.i(TAG, "Added secret key for new chat room: " + temp.getInt(Chat_Room.COL_ROOM_ID));
						}
						editor.apply();
					} catch (Exception e) {
						e.printStackTrace();

					}
				}break;
			}
			case MqttHeader.SEND_CHATROOM_SECRET: {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = pref.edit();
				try {
					JSONObject temp = new JSONObject(result);
					RSA rsa = new RSA(pref.getString(User.COL_PRIVATE_KEY, null), RSA.RSA_CONSTRUCT_WITH_PRIVATE);
					editor.putString(getRoomPrefKey(temp.getInt(Chat_Room.COL_ROOM_ID)), rsa.decryptKey(temp.getString(Chat_Room.COL_SECRET_KEY)));
					Log.i(TAG, "Added secret key for new chat room: " + temp.getInt(Chat_Room.COL_ROOM_ID));
					editor.apply();
				} catch (Exception e) {
					e.printStackTrace();
				}break;
			}
			case MqttHeader.GET_PUBLIC_KEY_REPLY:
			case MqttHeader.GET_PUBLIC_KEY_ROOM_REPLY: {
				//JSONArray of userID, pubKey
				if (!result.equals(MqttHeader.NO_RESULT)) {
					Log.i(TAG, "Public key reply: " + result);
					try {
						Chat_Room chat_room = (Chat_Room) args[0];
						JSONArray resultArr = new JSONArray(result);
						for (int i = 0; i < resultArr.length(); i++) {
							JSONObject temp = resultArr.getJSONObject(i);

							User user = new User();
							int userID = temp.getInt(User.COL_USER_ID);
							String publicKey = temp.getString(User.COL_PUBLIC_KEY);
							user.setUser_id(userID);
							user.setPublic_key(publicKey);

							publishEncryptedRoomSecret(chat_room, user);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}break;
			}
			case MqttHeader.GET_FORBIDDEN_SECRETS_REPLY:{
				if (!result.equals(MqttHeader.NO_RESULT)) {
					try {
						JSONArray resultArr = new JSONArray(result);
						for (int i = 0; i < resultArr.length(); i++) {
							JSONObject temp = resultArr.getJSONObject(i);

							User user = new User();
							int userID = temp.getInt(User.COL_USER_ID);
							String publicKey = temp.getString(User.COL_PUBLIC_KEY);
							Chat_Room chat_room = getOrGenerateRoomKey(context, temp.getInt(Chat_Room.COL_ROOM_ID));
							user.setUser_id(userID);
							user.setPublic_key(publicKey);

							publishEncryptedRoomSecret(chat_room, user);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}break;
		}
		return null;
	}

	private void publishEncryptedRoomSecret(Chat_Room chat_room, User user) {
		//requires user.user_id, user.public_key, chat_room.room_id
		String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);
		RSA rsa = new RSA(user.getPublic_key(), RSA.RSA_CONSTRUCT_WITH_PUBLIC);
		Chat_Room chat_room1 = new Chat_Room();
		chat_room1.setRoom_id(chat_room.getRoom_id());
		chat_room1.setSecret_key(rsa.encryptKey(chat_room.getSecret_key()));
		Log.i(TAG, "Encrypted secret key: " + chat_room1.getSecret_key());
		Object[] params = {user, chat_room1};
		//mqttHelperTemp.connectPublish(context, userTopic(user.getUser_id()), MqttHeader.SEND_CHATROOM_SECRET, chat_room);
		mqttHelper.connectPublish(context, uniqueTopic, MqttHeader.SET_CHATROOM_SECRET, params);
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		//any new asynctask should be executed here
		switch (header) {
		}
	}
}
