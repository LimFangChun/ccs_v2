package my.edu.tarc.communechat_v2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.UUID;

//import my.edu.tarc.communechat_v2.chatEngine.Encryption.RSA;

public class TestEncryptionActivity extends AppCompatActivity {

	SharedPreferences pref;
	private String uniqueTopic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_encryption);

		pref=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Button buttonTestRSA = findViewById(R.id.button_testRSA);
		buttonTestRSA.setOnClickListener(testRSA);
		uniqueTopic = UUID.randomUUID().toString().substring(0, 8);


	}

	private View.OnClickListener testRSA = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
//			List<Chat_Room> chatrooms = chatRoomRepository.getAllChatrooms();
//			Chat_Room lul = chatrooms.get(0);
//			String lql = new String(lul.getSecret_key());
//			Toast.makeText(TestEncryptionActivity.this, lql, Toast.LENGTH_SHORT).show();
//			RSA rsa = new RSA();
//			SharedPreferences.Editor editor = pref.edit();
//			String pubKeyString = new String(rsa.getPubKey());
//			editor.putString("public_key", pubKeyString);
//			editor.putString("private_key", new String(rsa.getPrivateKey()));
//			editor.apply();
//
//			User user = new User();
//			user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
//			user.setPublic_key(pubKeyString);
//
//			mqttHelper.connectPublishSubscribe(getApplicationContext(), uniqueTopic, MqttHeader.UPDATE_PUBLIC_KEY, user);
//			mqttHelper.getMqttClient().setCallback(new MqttCallback() {
//				@Override
//				public void connectionLost(Throwable cause) {
//
//				}
//
//				@Override
//				public void messageArrived(String topic, MqttMessage message) throws Exception {
//					mqttHelper.decode(message.toString());
//					if(mqttHelper.getReceivedHeader().equals(MqttHeader.UPDATE_PUBLIC_KEY_REPLY)) {
//						Toast.makeText(TestEncryptionActivity.this, mqttHelper.getReceivedResult(), Toast.LENGTH_SHORT).show();
//					}
//					mqttHelper.unsubscribe(uniqueTopic);
//				}
//
//				@Override
//				public void deliveryComplete(IMqttDeliveryToken token) {
//
//				}
//			});
//
		}
	};
}
