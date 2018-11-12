package my.edu.tarc.communechat_v2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import my.edu.tarc.communechat_v2.Fragment.ProfileFragment;

public class ProfileActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.profile_frag, new ProfileFragment())
				.commit();
	}
}
