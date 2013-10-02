package com.nish;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.nish.model.SharedPrefs;
import com.nish.model.Utility;
import com.nish.preference.ProfilePictureActivity;
import com.parse.Parse;
import com.parse.ParseUser;

public class PreferencesActivity extends PreferenceActivity {

	private static boolean show;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			setTheme(Utility.THEME);
			if (ParseUser.getCurrentUser() != null) {

				getPreferenceManager().setSharedPreferencesName(
						SharedPrefs.PREFS_NAME);
				addPreferencesFromResource(R.xml.prefs);

				PreferenceScreen logout = (PreferenceScreen) findPreference("logout");
				logout.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						logout();
						return true;
					}
				});

				PreferenceScreen back = (PreferenceScreen) findPreference("back");
				back.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						PreferencesActivity.this.finish();
						return true;
					}
				});

				CheckBoxPreference checkboxPref = (CheckBoxPreference) getPreferenceManager()
						.findPreference("location");
				checkboxPref.setChecked(ParseUser.getCurrentUser().getBoolean(
						"locationPrivacy"));
				checkboxPref
						.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
							public boolean onPreferenceChange(
									Preference preference, Object newValue) {
								if (newValue instanceof Boolean) {
									boolean boolVal = (Boolean) newValue;
									updateLocationPrivacy(boolVal);
								}
								return true;
							}
						});

				PreferenceScreen change = (PreferenceScreen) findPreference("profile_photo");
				change.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						PreferencesActivity.show = true;
						final String[] items = new String[] {
								"Remove current picture", "Take from camera",
								"Select from gallery", "Cancel" };
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								PreferencesActivity.this,
								android.R.layout.select_dialog_item, items);
						AlertDialog.Builder builder = new AlertDialog.Builder(
								PreferencesActivity.this);
						builder.setTitle("Select Image");
						builder.setAdapter(adapter,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int item) {
										if (item == 0) {
											removePic();
										} else if (item == 1) {
											Bundle sendBundle = new Bundle();
											sendBundle
													.putString("type", "take");
											sendBundle.putString("phase",
													"pref");

											Intent myIntent = new Intent(
													PreferencesActivity.this,
													ProfilePictureActivity.class);
											myIntent.putExtras(sendBundle);
											startActivity(myIntent);
										} else if (item == 2) {
											Bundle sendBundle = new Bundle();
											sendBundle.putString("type",
													"choose");
											sendBundle.putString("phase",
													"pref");

											Intent myIntent = new Intent(
													PreferencesActivity.this,
													ProfilePictureActivity.class);
											myIntent.putExtras(sendBundle);
											startActivity(myIntent);
										} else if (item == 3) {
											PreferencesActivity.show = false;
										}
									}
								});
						AlertDialog dialog = builder.create();
						if (show)
							dialog.show();
						return true;
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logout() {
		ParseUser.logOut();
		sendBroadcast(new Intent(WidgetActivity.FORCE_WIDGET_UPDATE));

		InfoActivity.h.sendEmptyMessage(0);
		Intent intent = new Intent(PreferencesActivity.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	private void removePic() {
		ParseUser pu = ParseUser.getCurrentUser();
		pu.remove("avatar");
		pu.saveInBackground();
	}

	private void updateLocationPrivacy(boolean value) {
		ParseUser pu = ParseUser.getCurrentUser();
		pu.put("locationPrivacy", value);
		pu.saveEventually();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Intent prefsIntent = new Intent(this.getApplicationContext(),
		// PreferencesActivity.class);
		//
		// MenuItem preferences = menu.findItem(R.id.settings_option_item);
		// preferences.setIntent(prefsIntent);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.startActivity(item.getIntent());
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}
