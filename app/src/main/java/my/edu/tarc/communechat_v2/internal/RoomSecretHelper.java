package my.edu.tarc.communechat_v2.internal;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.UUID;

import my.edu.tarc.communechat_v2.model.AdvancedEncryptionStandard;
import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.User;


public class RoomSecretHelper {

	private static final String TAG = "[RoomSecretHelper]";
    //todo: change to mqttHelper from main activity
    private static final MqttHelper roomSecretListener = new MqttHelper();
	private static final MqttHelper roomSecretMqttHelper = new MqttHelper();

	public static void initializeRoomSecretHelper(Context context, int userID) {
		refreshForbiddenSecrets(context, userID);
		listenIncomingSecrets(context, userID);
		getMyRoomSecrets(context, userID);
	}

	private static void refreshForbiddenSecrets(final Context context, int userID) {
		//Todo: request list of secret key need to be resent
        final String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);
		User user = new User();
		user.setUser_id(userID);
		roomSecretMqttHelper.connectPublishSubscribe(context, uniqueTopic, MqttHeader.GET_FORBIDDEN_SECRETS, user);
		//gets array of user_id, room_id, public_key
		roomSecretMqttHelper.getMqttClient().setCallback(new MqttCallback() {
			@Override
			public void connectionLost(Throwable cause) {

			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
                roomSecretMqttHelper.unsubscribe(topic);
				new AsyncMqttMessageHandler(context, message.toString()).execute();
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {

			}
		});

	}

	public static void requestKey(Context context, User me, Chat_Room chat_room) {
		String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);
		//Todo: request specific room key from db
		//although this is currently not used
		//but will probably be needed for the future
		Object[] params = {me, chat_room};
		roomSecretMqttHelper.connectPublishSubscribe(context, uniqueTopic, MqttHeader.GET_CHATROOM_SECRET, params);
	}

	private static String userTopic(int userID) {
		return "roomSecret/".concat(Integer.toString(userID));
	}

	public static String getRoomPrefKey(int room_id) {
		return Chat_Room.COL_SECRET_KEY.concat("/chatroom".concat(Integer.toString(room_id)));
	}

	//Use this when starting a chat room.
	public static void sendRoomSecret(final Context context, final User user, Chat_Room chat_room) {
		final Chat_Room chat_room1 = getOrGenerateRoomKey(context, chat_room.getRoom_id());
        final String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);

		//get public from server
		roomSecretMqttHelper.connectPublishSubscribe(context, uniqueTopic, MqttHeader.GET_PUBLIC_KEY, user);
		roomSecretMqttHelper.getMqttClient().setCallback(new MqttCallback() {
			@Override
			public void connectionLost(Throwable cause) {

			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				roomSecretMqttHelper.unsubscribe(topic);
				new AsyncMqttMessageHandler(context, message.toString()).execute(chat_room1);
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {

			}
		});
	}

	//Use this when starting a group chat room.
	public static void sendRoomSecret(final Context context, Chat_Room chat_room) {
		final Chat_Room chat_room1 = getOrGenerateRoomKey(context, chat_room.getRoom_id());
        final String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);

		//get public from server
		roomSecretMqttHelper.connectPublishSubscribe(context, uniqueTopic, MqttHeader.GET_PUBLIC_KEY_ROOM, chat_room1);
		roomSecretMqttHelper.getMqttClient().setCallback(new MqttCallback() {
			@Override
			public void connectionLost(Throwable cause) {

			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				roomSecretMqttHelper.unsubscribe(topic);
				new AsyncMqttMessageHandler(context, message.toString()).execute(chat_room1);
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {

			}
		});
	}

    private static void listenIncomingSecrets(final Context context, final int userID) {
		//subscribe to my own topic
		roomSecretListener.connectSubscribe(context, userTopic(userID));
		//listen to incoming secret key
		roomSecretListener.getMqttClient().setCallback(new MqttCallback() {
			@Override
			public void connectionLost(Throwable cause) {

			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				new AsyncMqttMessageHandler(context, message.toString()).execute();
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {

			}
		});
	}

	private static void getMyRoomSecrets(final Context context, int userID) {
        final String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);
		User user = new User();
		user.setUser_id(userID);
		roomSecretMqttHelper.connectPublishSubscribe(context, uniqueTopic, MqttHeader.GET_CHATROOM_SECRET_ALL, user);
		roomSecretMqttHelper.getMqttClient().setCallback(new MqttCallback() {
			@Override
			public void connectionLost(Throwable cause) {

			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
                roomSecretMqttHelper.unsubscribe(topic);
				new AsyncMqttMessageHandler(context, message.toString()).execute();
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {

			}
		});
	}

	public static Chat_Room getOrGenerateRoomKey(Context context, int room_id) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return getOrGenerateRoomKey(pref, room_id);
	}

	private static Chat_Room getOrGenerateRoomKey(SharedPreferences pref, int room_id) {
		Chat_Room chat_room = new Chat_Room();
		chat_room.setRoom_id(room_id);
		SharedPreferences.Editor editor = pref.edit();
		String roomKey = pref.getString(getRoomPrefKey(chat_room.getRoom_id()), "");
		Log.i(TAG, "preference: " + getRoomPrefKey(chat_room.getRoom_id()));
		Log.i(TAG, "chatroom: " + chat_room.getRoom_id());
		Log.i(TAG, "roomKey value: " + roomKey);
		if (roomKey.equals("")) {
			//generate a new one if no room key is found
			Log.i(TAG, "Generating a new room key.");
			AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard();
			roomKey = aes.getKey();
			editor.putString(getRoomPrefKey(chat_room.getRoom_id()), roomKey);
			Log.i(TAG, "new key: " + aes.getKey());
			boolean complete = editor.commit();
			Log.i(TAG, "commit success: " + Boolean.toString(complete));
		}
		chat_room.setSecret_key(roomKey);
		return chat_room;
	}

//	private static class HandleMessageAsync extends AsyncTask<Object, Void, Void> {
//		@SuppressLint("StaticFieldLeak")
//		Context context;
//		MqttHelper mqttHelper;
//		Object[] params;
//		String header;
//		String result;
//
//		public HandleMessageAsync(Context context, String message) {
//			super();
//			mqttHelper = new MqttHelper(); //for decode message only.
//			mqttHelper.decode(message);
//			this.context = context;
//		}
//
//		@Override
//		protected Void doInBackground(Object... objects) {
//			params = objects;
//			header = mqttHelper.getReceivedHeader();
//			result = mqttHelper.getReceivedResult();
//
//
//			switch (header) {
//				case MqttHeader.GET_CHATROOM_SECRET_ALL_REPLY: {
//					if (!result.equals(MqttHeader.NO_RESULT)) {
//						//decrypt and store
//						SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
//						SharedPreferences.Editor editor = pref.edit();
//						try {
//							//returns array of roomID and secretKey
//							JSONArray resultArr = new JSONArray();
//							//JSONObject temp = new JSONObject(result);
//							for (int i = 0; i < resultArr.length(); i++) {
//								JSONObject temp = resultArr.getJSONObject(i);
//								RSA rsa = new RSA(pref.getString(User.COL_PRIVATE_KEY, null), RSA.RSA_CONSTRUCT_WITH_PRIVATE);
//								editor.putString(getRoomPrefKey(temp.getInt(Chat_Room.COL_ROOM_ID)), rsa.decryptKey(temp.getString(Chat_Room.COL_SECRET_KEY)));
//								Log.i(TAG, "Added secret key for new chat room: " + temp.getInt(Chat_Room.COL_ROOM_ID));
//							}
//							editor.apply();
//						} catch (Exception e) {
//							e.printStackTrace();
//
//						}
//					}break;
//				}
//				case MqttHeader.SEND_CHATROOM_SECRET: {
//					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
//					SharedPreferences.Editor editor = pref.edit();
//					try {
//						JSONObject temp = new JSONObject(result);
//						RSA rsa = new RSA(pref.getString(User.COL_PRIVATE_KEY, null), RSA.RSA_CONSTRUCT_WITH_PRIVATE);
//						editor.putString(getRoomPrefKey(temp.getInt(Chat_Room.COL_ROOM_ID)), rsa.decryptKey(temp.getString(Chat_Room.COL_SECRET_KEY)));
//						Log.i(TAG, "Added secret key for new chat room: " + temp.getInt(Chat_Room.COL_ROOM_ID));
//						editor.apply();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}break;
//				}
//				case MqttHeader.GET_PUBLIC_KEY_REPLY: {
//					//JSONArray of userID, pubKey
//					if (!result.equals(MqttHeader.NO_RESULT)) {
//						Chat_Room chat_room = (Chat_Room) params[0];
//
//						try {
//							Chat_Room chat_room1 = getOrGenerateRoomKey(context, chat_room.getRoom_id());
//							JSONArray resultArr = new JSONArray(result);
//							for (int i = 0; i < resultArr.length(); i++) {
//								JSONObject temp = resultArr.getJSONObject(i);
//
//								User user = new User();
//								int userID = temp.getInt(User.COL_USER_ID);
//								String publicKey = temp.getString(User.COL_PUBLIC_KEY);
//								user.setUser_id(userID);
//								user.setPublic_key(publicKey);
//
//								publishEncryptedRoomSecret(chat_room1, user);
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}break;
//				}
//				case MqttHeader.GET_FORBIDDEN_SECRETS_REPLY:{
//					if (!result.equals(MqttHeader.NO_RESULT)) {
//						try {
//							JSONArray resultArr = new JSONArray(result);
//							for (int i = 0; i < resultArr.length(); i++) {
//								JSONObject temp = resultArr.getJSONObject(i);
//
//								User user = new User();
//								int userID = temp.getInt(User.COL_USER_ID);
//								String publicKey = temp.getString(User.COL_PUBLIC_KEY);
//								Chat_Room chat_room = getOrGenerateRoomKey(context, temp.getInt(Chat_Room.COL_ROOM_ID));
//								user.setUser_id(userID);
//								user.setPublic_key(publicKey);
//
//								publishEncryptedRoomSecret(chat_room, user);
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}break;
//			}
//			return null;
//		}
//
//		private void publishEncryptedRoomSecret(Chat_Room chat_room, User user) {
//			//requires user.user_id, user.public_key, chat_room.room_id
//			String uniqueTopic = UUID.randomUUID().toString().substring(0, 8);
//			RSA rsa = new RSA(user.getPublic_key(), RSA.RSA_CONSTRUCT_WITH_PUBLIC);
//			chat_room.setSecret_key(rsa.encryptKey(chat_room.getSecret_key()));
//
//			Object[] params = {user, chat_room};
//			//Todo: tell the client to fetch new key from db, not send directly
//			//mqttHelper.connectPublish(context, userTopic(user.getUser_id()), MqttHeader.SEND_CHATROOM_SECRET, chat_room);
//			mqttHelper.connectPublish(context, uniqueTopic, MqttHeader.SET_CHATROOM_SECRET, params);
//		}
//
//		@Override
//		protected void onPostExecute(Void aVoid) {
//			super.onPostExecute(aVoid);
//			//any new asynctask should be executed here
//			switch (header) {
//			}
//		}
//	}
}
