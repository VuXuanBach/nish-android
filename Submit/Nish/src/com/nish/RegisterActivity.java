package com.nish;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.model.Utility;
import com.nish.preference.ProfilePictureActivity;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class RegisterActivity extends SherlockActivity {

	private boolean isEmail, isUsername, isPassword, avatar, show;
	private Context context;
	public static ImageView ivphoto;
	private ProgressDialog pdialog;
	private String username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");

			setTheme(Utility.THEME);
			Utility.displayErrorConnection(this);
			context = this;
			setRequestedOrientation(Utility.NOSENSOR);
			if (Utility.checkCurrentAndroidVersion())
				requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_registration);
			setTheme(Utility.THEME);
			super.onCreate(savedInstanceState);

			Button register = (Button) findViewById(R.id.btn_register);
			register.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					register();
				}
			});

			ivphoto = (ImageView) findViewById(R.id.photoRegister);
			ivphoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					show = true;
					final String[] items = new String[] {
							"Remove current picture", "Take from camera",
							"Select from gallery", "Cancel" };
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(
							RegisterActivity.this,
							android.R.layout.select_dialog_item, items);
					AlertDialog.Builder builder = new AlertDialog.Builder(
							RegisterActivity.this);
					builder.setTitle("Select Image");
					builder.setAdapter(adapter,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									if (item == 0) {
										removePic();
									} else if (item == 1) {
										Bundle sendBundle = new Bundle();
										sendBundle.putString("type", "take");
										sendBundle.putString("phase",
												"register");

										Intent myIntent = new Intent(
												RegisterActivity.this,
												ProfilePictureActivity.class);
										myIntent.putExtras(sendBundle);
										startActivity(myIntent);
										avatar = true;
									} else if (item == 2) {
										Bundle sendBundle = new Bundle();
										sendBundle.putString("type", "choose");
										sendBundle.putString("phase",
												"register");

										Intent myIntent = new Intent(
												RegisterActivity.this,
												ProfilePictureActivity.class);
										myIntent.putExtras(sendBundle);
										startActivity(myIntent);
										avatar = true;
									} else if (item == 3) {
										show = false;
									}
								}
							});
					AlertDialog dialog = builder.create();
					if (show)
						dialog.show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void register() {
		isEmail = isUsername = isPassword = false;
		final ParseUser user = new ParseUser();

		TextView tvEmail = (TextView) findViewById(R.id.email);
		TextView tvUsername = (TextView) findViewById(R.id.username);
		TextView tvPassword = (TextView) findViewById(R.id.password);
		TextView tvPhone = (TextView) findViewById(R.id.phone);

		String email = tvEmail.getText().toString();
		username = tvUsername.getText().toString();
		password = tvPassword.getText().toString();
		String phone = tvPhone.getText().toString();

		if (!Utility.validateEmail(email)) {
			tvEmail.setError("Please enter valid email address");
			isEmail = false;
		} else {
			tvEmail.setError(null);
			isEmail = true;
		}
		if (!Utility.validateNullField(username)) {
			tvUsername.setError("Please enter your username");
			isUsername = false;
		} else {
			tvUsername.setError(null);
			isUsername = true;
		}
		if (!Utility.validateNullField(password)) {
			tvPassword.setError("Please enter your password");
			isPassword = false;
		} else {
			tvPassword.setError(null);
			isPassword = true;
		}
		if (!Utility.validateNullField(phone)) {
			phone = "";
		}

		if (isEmail && isUsername && isPassword) {
			if (!Utility.isOnline(this)) {
				Utility.displayDialog(context, "Error", "No Internet access");
			} else {
				user.setUsername(username);
				user.setPassword(password);
				user.setEmail(email);
				user.put("phone", phone);
				user.put("locationPrivacy", false);
				pdialog = ProgressDialog
						.show(this, "", "Registering ...", true);
				user.signUpInBackground(new SignUpCallback() {

					public void done(ParseException e) {
						if (e == null) {
							if (avatar) {
								uploadImage(user);
							} else {
								
								login();
							}
						} else {
							pdialog.dismiss();
							Utility.displayDialog(context, "Error",
									"This email/username is already been used");
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	private void removePic() {
		ivphoto.setImageResource(R.drawable.default_avatar);
		avatar = false;
	}

	private void uploadImage(ParseUser pu) {
		final ParseUser u = pu;
		Utility.displayErrorConnection(this);
		try {
			byte[] byte_img_data = Utility.convertFileToBytes(Environment
					.getExternalStorageDirectory() + "/Nish/avatar.jpg");
			final ParseFile file = new ParseFile("avatar.jpg", byte_img_data);
			file.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					if (e == null) {
						u.put("avatar", file);
						u.saveInBackground();
						login();
					} else {
						pdialog.dismiss();
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			pdialog.dismiss();
			e.printStackTrace();
		}
	}

	private void login() {
		ParseQuery.clearAllCachedResults();
		loadFriendToSqlite();
		loadPendingToSqlite();
		sendBroadcast(new Intent(
				WidgetActivity.FORCE_WIDGET_UPDATE));
		Intent myIntent = new Intent(RegisterActivity.this,
				TabMainActivity.class);
		startActivityForResult(myIntent, 1);
		pdialog.dismiss();
		RegisterActivity.this.finish();
	}

	private void loadFriendToSqlite() {
		if (ParseUser.getCurrentUser() != null) {
			ParseUser currUser = ParseUser.getCurrentUser();

			SQLiteDatabase myDb = openOrCreateDatabase("nish_user.db",
					Context.MODE_PRIVATE, null);
			myDb.execSQL("drop table if exists friend");
			myDb.execSQL("create table friend ( _id integer primary key autoincrement, friendId text not null);");

			ParseQuery query = new ParseQuery("Friend");
			query.include("user1");
			query.include("user2");
			query.whereEqualTo("user1", currUser);

			query.findInBackground(new FindCallback() {

				@Override
				public void done(List<ParseObject> friends, ParseException e) {
					if (e == null) {
						for (ParseObject fr : friends) {
							SQLiteDatabase myDb = openOrCreateDatabase(
									"nish_user.db", Context.MODE_PRIVATE, null);
							ContentValues newValues = new ContentValues();
							ParseUser pu = fr.getParseUser("user2");
							newValues.put("friendId", pu.getObjectId());
							myDb.insert("friend", null, newValues);
							myDb.close();
						}
					} else {
						e.printStackTrace();
					}
				}
			});

			ParseQuery query2 = new ParseQuery("Friend");
			query2.include("user1");
			query2.include("user2");
			query2.whereEqualTo("user2", currUser);

			query2.findInBackground(new FindCallback() {

				@Override
				public void done(List<ParseObject> friends, ParseException e) {
					if (e == null) {
						for (ParseObject fr : friends) {
							SQLiteDatabase myDb = openOrCreateDatabase(
									"nish_user.db", Context.MODE_PRIVATE, null);
							ContentValues newValues = new ContentValues();
							ParseUser pu = fr.getParseUser("user1");
							newValues.put("friendId", pu.getObjectId());
							myDb.insert("friend", null, newValues);
							myDb.close();
						}
					} else {
						e.printStackTrace();
					}
				}
			});
			myDb.close();
		}
	}

	private void loadPendingToSqlite() {
		if (ParseUser.getCurrentUser() != null) {
			ParseUser currUser = ParseUser.getCurrentUser();

			SQLiteDatabase myDb = openOrCreateDatabase("nish_user.db",
					Context.MODE_PRIVATE, null);
			myDb.execSQL("drop table if exists pending");
			myDb.execSQL("create table pending ( _id integer primary key autoincrement, pendingId text not null);");

			ParseQuery query = new ParseQuery("Pending");
			query.include("user1");
			query.include("user2");
			query.whereEqualTo("user1", currUser);

			query.findInBackground(new FindCallback() {

				@Override
				public void done(List<ParseObject> friends, ParseException e) {
					if (e == null) {
						for (ParseObject fr : friends) {
							SQLiteDatabase myDb = openOrCreateDatabase(
									"nish_user.db", Context.MODE_PRIVATE, null);
							ContentValues newValues = new ContentValues();
							ParseUser pu = fr.getParseUser("user2");
							newValues.put("pendingId", pu.getObjectId());
							myDb.insert("pending", null, newValues);
							myDb.close();
						}
					} else {
						e.printStackTrace();
					}
				}
			});

			ParseQuery query2 = new ParseQuery("Pending");
			query2.include("user1");
			query2.include("user2");
			query2.whereEqualTo("user2", currUser);

			query2.findInBackground(new FindCallback() {

				@Override
				public void done(List<ParseObject> friends, ParseException e) {
					if (e == null) {
						for (ParseObject fr : friends) {
							SQLiteDatabase myDb = openOrCreateDatabase(
									"nish_user.db", Context.MODE_PRIVATE, null);
							ContentValues newValues = new ContentValues();
							ParseUser pu = fr.getParseUser("user1");
							newValues.put("pendingId", pu.getObjectId());
							myDb.insert("pending", null, newValues);
							myDb.close();
						}
					} else {
						e.printStackTrace();
					}
				}
			});
			myDb.close();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		menu.add("Back").setIcon(R.drawable.ic_prev)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("Refresh").setIcon(R.drawable.ic_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("back")) {
			Intent myIntent = new Intent(RegisterActivity.this,
					MainActivity.class);
			startActivityForResult(myIntent, 1);
			finish();
		}
		if (item.getTitle().toString().equalsIgnoreCase("Refresh")) {
			TextView tvEmail = (TextView) findViewById(R.id.email);
			TextView tvUsername = (TextView) findViewById(R.id.username);
			TextView tvPassword = (TextView) findViewById(R.id.password);
			TextView tvPhone = (TextView) findViewById(R.id.phone);

			tvEmail.setText("");
			tvUsername.setText("");
			tvPassword.setText("");
			tvPhone.setText("");
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}
