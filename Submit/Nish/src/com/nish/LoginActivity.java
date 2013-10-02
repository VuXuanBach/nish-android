package com.nish;

import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.model.Utility;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class LoginActivity extends SherlockActivity {
	private Context context;
	private boolean isUsername, isPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			if (Utility.checkCurrentAndroidVersion())
				requestWindowFeature(Window.FEATURE_NO_TITLE);

			setRequestedOrientation(Utility.NOSENSOR);
			setContentView(R.layout.activity_login);
			setTheme(Utility.THEME);
			context = this;
			Utility.displayErrorConnection(this);
			retrieveData();
			Button login = (Button) findViewById(R.id.btn_login);
			login.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checkLogin();
				}
			});

			TextView pass = (TextView) findViewById(R.id.tv_forget_password);
			pass.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent myIntent = new Intent(LoginActivity.this,
							ForgetPasswordActivity.class);
					startActivityForResult(myIntent, 1);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void checkLogin() {
		isUsername = isPassword = false;
		TextView tvUsername = (TextView) findViewById(R.id.txt_username);
		TextView tvPassword = (TextView) findViewById(R.id.txt_password);

		String username = tvUsername.getText().toString();
		String password = tvPassword.getText().toString();

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

		if (isUsername && isPassword) {
			if (!Utility.isOnline(this)) {
				Utility.displayDialog(context, "Error", "No Internet access");
			} else {

				final ProgressDialog pdialog = ProgressDialog.show(this, "",
						"Loging in...", true);
				ParseUser.logInInBackground(username, password,
						new LogInCallback() {

							@Override
							public void done(ParseUser user, ParseException e) {
								if (e == null) {
									if (user != null) {
										CheckBox rb = (CheckBox) findViewById(R.id.cb_rememberUP);
										if (rb.isChecked()) {
											String username = ((TextView) findViewById(R.id.txt_username))
													.getText().toString();
											String password = ((TextView) findViewById(R.id.txt_password))
													.getText().toString();
											rememberUP(username, password);
										} else {
											removeTable();
										}
										loadFriendToSqlite();
										loadPendingToSqlite();
										pdialog.dismiss();
										sendBroadcast(new Intent(
												WidgetActivity.FORCE_WIDGET_UPDATE));
										Intent myIntent = new Intent(
												LoginActivity.this,
												TabMainActivity.class);
										startActivityForResult(myIntent, 1);
										finish();
									} else {
										Utility.displayDialog(context, "Error",
												"Invalid Username/Password");
										pdialog.dismiss();

									}
								} else {
									Utility.displayDialog(context, "Error",
											"Invalid Username/Password");
									e.printStackTrace();
									pdialog.dismiss();
								}
							}
						});
			}
		}
	}

	private void rememberUP(String username, String password) {
		SQLiteDatabase myDb = openOrCreateDatabase("nish_user.db",
				Context.MODE_PRIVATE, null);
		myDb.execSQL("drop table if exists user");
		myDb.execSQL("create table user ( _id integer primary key autoincrement, username text not null, password text not null);");
		ContentValues newValues = new ContentValues();
		newValues.put("username", username);
		newValues.put("password", password);
		myDb.insert("user", null, newValues);
		myDb.close();
	}

	private void removeTable() {
		SQLiteDatabase myDb = openOrCreateDatabase("nish_user.db",
				Context.MODE_PRIVATE, null);
		myDb.execSQL("drop table if exists user");
		myDb.close();
	}

	private void retrieveData() {
		SQLiteDatabase myDb = openOrCreateDatabase("nish_user.db",
				Context.MODE_PRIVATE, null);
		myDb.execSQL("create table if not exists user ( _id integer primary key autoincrement, username text not null, password text not null);");
		Cursor user = myDb.query("user", null, null, null, null, null, null);
		if (user.moveToFirst()) {
			String username = user.getString(1);
			String password = user.getString(2);
			TextView uV = (TextView) findViewById(R.id.txt_username);
			TextView pV = (TextView) findViewById(R.id.txt_password);
			uV.setText(username);
			pV.setText(password);
			CheckBox cb = (CheckBox) findViewById(R.id.cb_rememberUP);
			cb.setChecked(true);
		}
		myDb.close();
	}

	private void loadFriendToSqlite() {
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

	private void loadPendingToSqlite() {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Back").setIcon(R.drawable.ic_prev)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("Refresh").setIcon(R.drawable.ic_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("back")) {
			Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
			startActivityForResult(myIntent, 1);
			finish();
		}
		if (item.getTitle().toString().equalsIgnoreCase("Refresh")) {
			TextView tvUsername = (TextView) findViewById(R.id.txt_username);
			TextView tvPassword = (TextView) findViewById(R.id.txt_password);

			tvUsername.setText("");
			tvPassword.setText("");
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}
