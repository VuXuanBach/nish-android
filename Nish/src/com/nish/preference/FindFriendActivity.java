package com.nish.preference;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.R;
import com.nish.model.FriendAdapter;
import com.nish.model.FriendRow;
import com.nish.model.Utility;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FindFriendActivity extends SherlockActivity {
	private ListView layout;
	private boolean isUsername;
	private ImageView avatarImageView;
	private FriendAdapter fa;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			Utility.displayErrorConnection(this);
			setRequestedOrientation(Utility.NOSENSOR);
			setContentView(R.layout.activity_find_friend);
			setTheme(Utility.THEME);
			fa = new FriendAdapter(this);
			Button btnSearch = (Button) findViewById(R.id.btn_search);
			btnSearch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Utility.displayErrorConnection(FindFriendActivity.this);
					searchFriend();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Back").setIcon(R.drawable.ic_prev)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("back")) {
			finish();
		}
		return false;
	}

	public void searchFriend() {
		fa.getArray().clear();

		TextView tvusername = (TextView) findViewById(R.id.txt_search);
		String username = tvusername.getText().toString();
		if (!Utility.validateNullField(username)) {
			tvusername.setError("Please enter");
			isUsername = false;
		} else {
			tvusername.setError(null);
			isUsername = true;
		}
		if (isUsername) {
			layout = (ListView) findViewById(R.id.friend_list_view);
			if (Utility.isOnline(this)) {
				final ProgressDialog pdialog = ProgressDialog.show(this, "",
						"Searching ...", true);
				ParseQuery query = ParseUser.getQuery();
				query.whereContains("username", username);
				ListView listview = (ListView) findViewById(com.nish.R.id.friend_list_view);
				listview.setAdapter(fa);
				query.findInBackground(new FindCallback() {

					@Override
					public void done(List<ParseObject> users, ParseException e) {
						if (e == null) {
							for (ParseObject u : users) {
								ParseUser pu = (ParseUser) u;
								if (ParseUser.getCurrentUser() != null) {
									ParseUser curr = ParseUser.getCurrentUser();
									if (!curr.getObjectId().equals(
											u.getObjectId())
											&& !friendFound(pu)
											&& !pendingFound(pu)) {
										fa.getArray().add(new FriendRow(pu));
										fa.notifyDataSetChanged();
									}
								}
							}
							
							pdialog.dismiss();
						} else {
							pdialog.dismiss();
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	private boolean friendFound(ParseUser pu) {
		SQLiteDatabase myDb = openOrCreateDatabase("nish_user.db",
				Context.MODE_PRIVATE, null);
		final String SQL_STATEMENT = "SELECT * FROM friend WHERE friendId=?";
		Cursor cur = myDb.rawQuery(SQL_STATEMENT,
				new String[] { pu.getObjectId() });
		int count = cur.getCount();
		myDb.close();
		return (count > 0) ? true : false;
	}

	private boolean pendingFound(ParseUser pu) {
		SQLiteDatabase myDb = openOrCreateDatabase("nish_user.db",
				Context.MODE_PRIVATE, null);
		final String SQL_STATEMENT = "SELECT * FROM pending WHERE pendingId=?";
		Cursor cur = myDb.rawQuery(SQL_STATEMENT,
				new String[] { pu.getObjectId() });
		int count = cur.getCount();
		myDb.close();
		return (count > 0) ? true : false;
	}
}