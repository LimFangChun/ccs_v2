package my.edu.tarc.communechat_v2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.UUID;

import my.edu.tarc.communechat_v2.ChatEngine.RSA;
import my.edu.tarc.communechat_v2.internal.MqttHeader;
import my.edu.tarc.communechat_v2.model.User;

import static my.edu.tarc.communechat_v2.MainActivity.mqttHelper;

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
			RSA rsa = new RSA();
			SharedPreferences.Editor editor = pref.edit();
			String pubKeyString = new String(rsa.getPubKey());
			editor.putString("public_key", pubKeyString);
			editor.putString("private_key", new String(rsa.getPrivateKey()));
			editor.apply();

			User user = new User();
			user.setUser_id(pref.getInt(User.COL_USER_ID, -1));
			user.setPublic_key(pubKeyString);

			mqttHelper.connectPublishSubscribe(getApplicationContext(), uniqueTopic, MqttHeader.UPDATE_PUBLIC_KEY, user);
			mqttHelper.getMqttClient().setCallback(new MqttCallback() {
				@Override
				public void connectionLost(Throwable cause) {

				}

				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					mqttHelper.decode(message.toString());
					if(mqttHelper.getReceivedHeader().equals(MqttHeader.UPDATE_PUBLIC_KEY_REPLY)) {
						Toast.makeText(TestEncryptionActivity.this, mqttHelper.getReceivedResult(), Toast.LENGTH_SHORT).show();
					}
					mqttHelper.unsubscribe(uniqueTopic);
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {

				}
			});

		}
	};
}
