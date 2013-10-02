package com.nish;

import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.model.FriendRow;
import com.nish.model.ListFriendAdapter;
import com.nish.model.ListPendingAdapter;
import com.nish.model.Utility;
import com.nish.preference.FindFriendActivity;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FriendActivity extends SherlockActivity {

	private final int ONE = 0;
	private final int TWO = 1;
	private ViewFlipper vf;
	private ListFriendAdapter la;
	private ListPendingAdapter pa;
	public static Handler h;
	private int done, users, done1, pending;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			done = 0;
			users = 0;
			done1 = 0;
			pending = 0;
			setContentView(R.layout.activity_friend);
			vf = (ViewFlipper) findViewById(R.id.flipper_news);

			vf.addView(View.inflate(this, R.layout.list, null), ONE);
			vf.addView(View.inflate(this, R.layout.list1, null), TWO);
			h = new Handler() {
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch (msg.what) {
					case 0:
						finish();
						break;

					}
				}
			};
			Utility.displayErrorConnection(this);
			vf.setDisplayedChild(ONE);
			listFriend();
			Button btnSearch = (Button) findViewById(R.id.btn_search_friend);
			btnSearch.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEND) {
						listFriend();
						return true;
					}
					return false;
				}
			});
			btnSearch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Utility.displayErrorConnection(FriendActivity.this);
					listFriend();
				}
			});
			TextView tab1 = (TextView) findViewById(R.id.toolbar_tab_follow);
			tab1.setSelected(true);
			tab1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Utility.displayErrorConnection(FriendActivity.this);
					listFriend();
					selectTab(true, false);
				}
			});

			TextView tab2 = (TextView) findViewById(R.id.toolbar_tab_you);
			tab2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Utility.displayErrorConnection(FriendActivity.this);
					listPending();
					selectTab(false, true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void listFriend() {
		vf.setDisplayedChild(ONE);
		if (Utility.isOnline(FriendActivity.this)) {
			if (la != null) {
				if (users > done) {
					return;
				} else {
					done = 0;
				}
			}
			TextView tvusername = (TextView) findViewById(R.id.txt_search_friend);
			final String username = tvusername.getText().toString();
			if (ParseUser.getCurrentUser() != null) {
				ParseUser currUser = ParseUser.getCurrentUser();
				la = new ListFriendAdapter(FriendActivity.this);
				la.setFa(this);

				ListView listview = (ListView) findViewById(com.nish.R.id.list_view);
				listview.setAdapter(la);
				for (int i = 0; i < 2; i++) {
					final int count = i;
					ParseQuery query = new ParseQuery("Friend");
					query.include("user1");
					query.include("user2");

					if (count == 0)
						query.whereEqualTo("user1", currUser);
					else
						query.whereEqualTo("user2", currUser);
					query.findInBackground(new FindCallback() {

						@Override
						public void done(List<ParseObject> pendings,
								ParseException e) {
							if (e == null) {
								users = pendings.size();
								for (ParseObject pending : pendings) {
									ParseUser pu;
									if (count == 0)
										pu = pending.getParseUser("user2");
									else
										pu = pending.getParseUser("user1");
									done++;
									if (pu.getUsername().contains(username)) {
										la.getArray().add(
												new FriendRow(pu, pending));
										la.notifyDataSetChanged();
									}
								}
							} else {
								e.printStackTrace();
							}
						}
					});
				}
			}
		}
	}

	private void listPending() {
		vf.setDisplayedChild(TWO);
		if (Utility.isOnline(FriendActivity.this)) {
			if (pa != null) {
				if (pending > done1) {
					return;
				} else {
					done1 = 0;
				}
			}
			if (ParseUser.getCurrentUser() != null) {
				ParseUser currUser = ParseUser.getCurrentUser();
				ParseQuery query = new ParseQuery("Pending");
				query.include("user1");
				query.include("user2");
				query.whereEqualTo("user1", currUser);

				pa = new ListPendingAdapter(FriendActivity.this);
				ListView listview = (ListView) findViewById(com.nish.R.id.list_view1);
				listview.setAdapter(pa);
				query.findInBackground(new FindCallback() {

					@Override
					public void done(List<ParseObject> pendings,
							ParseException e) {
						if (e == null) {
							pending = pendings.size();
							for (ParseObject pending : pendings) {
								ParseUser pu = pending.getParseUser("user2");
								pa.getArray().add(new FriendRow(pu, pending));
								done++;
							}
							pa.notifyDataSetChanged();
						} else {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	private void selectTab(boolean tab1, boolean tab2) {
		TextView tab11 = (TextView) findViewById(R.id.toolbar_tab_follow);
		TextView tab22 = (TextView) findViewById(R.id.toolbar_tab_you);
		tab11.setSelected(tab1);
		tab22.setSelected(tab2);
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
			finish();
		}
		if (item.getTitle().toString().equalsIgnoreCase("refresh")) {
			try {
				if (vf.getDisplayedChild() == ONE) {
					listFriend();
				} else {
					listPending();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}