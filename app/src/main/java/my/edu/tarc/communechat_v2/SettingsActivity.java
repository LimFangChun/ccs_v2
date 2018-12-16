package my.edu.tarc.communechat_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
	private static final String TAG = SettingsActivity.class.getSimpleName();
	public static final int CHOOSE_FILE_REQUESTCODE = 1001;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// load settings fragment
		getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
	}

	public static class MainPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings);


			// notification preference change listener
			bindPreferenceSummaryToValue(findPreference(getString(R.string.ring_tone_pref)));
			Preference storagePath = findPreference("storagePath");
			storagePath.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
					//intent.setType("file/*");
					Intent i = Intent.createChooser(intent, "File");
					startActivityForResult(i, CHOOSE_FILE_REQUESTCODE);
					return false;
				}
			});
			setHasOptionsMenu(true);
			bindPreferenceSummaryToValue(findPreference("storagePath"));

		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	private static void bindPreferenceSummaryToValue(Preference preference) {
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);


		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
				PreferenceManager
						.getDefaultSharedPreferences(preference.getContext())
						.getString(preference.getKey(), ""));

	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {

		public boolean onPreferenceChange(Preference preference, Object newValue) {
			String stringValue = newValue.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference.setSummary(
						index >= 0
								? listPreference.getEntries()[index]
								: null);

			} else if (preference instanceof RingtonePreference) {
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);

				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(
							preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(R.string.summary_choose_ringtone);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.

						String name = ringtone.getTitle(preference.getContext());
						preference.setSummary(name);
					}

				}
			}  else {
				preference.setSummary(stringValue);
			}
			return true;
		}
	};



	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "Data: " + data.getDataString());
		if (requestCode == CHOOSE_FILE_REQUESTCODE && resultCode == RESULT_OK){
			String path = data.getDataString(); //path format matters, change if related methods are not working.
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = preferences.edit();
			Log.i(TAG, "Path: " + path);
			editor.putString(getString(R.string.pref_storagePath), path);
			editor.commit();
		}
	}
}