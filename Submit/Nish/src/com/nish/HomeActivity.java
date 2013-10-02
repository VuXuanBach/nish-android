package com.nish;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.model.HomeAdapter;
import com.nish.model.HomePost;
import com.nish.model.Utility;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;

public class HomeActivity extends SherlockActivity {

	private HomeAdapter ha;
	public static ParseObject current;
	private int num_image;
	ListView listview;
	public static Handler h;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			setContentView(R.layout.activity_home);
			setRequestedOrientation(Utility.NOSENSOR);
			setTheme(Utility.THEME);
			getSupportActionBar();
			num_image = 5;
			ha = new HomeAdapter(this);
			Utility.displayErrorConnection(this);
			Button btnLoadMore = new Button(this);
			btnLoadMore.setText("Load More");
			listview = (ListView) findViewById(R.id.homeTable);
			listview.addFooterView(btnLoadMore);
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
			btnLoadMore.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					loadMoreListView();
				}
			});
			getRelatedImages(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadMoreListView() {
		num_image += 5;
		int currentPosition = listview.getFirstVisiblePosition();
		int lastPosition = listview.getLastVisiblePosition();
		Log.d("Position", currentPosition + "  -  " + lastPosition);
		getRelatedImages(false);
		listview.setSelectionFromTop(lastPosition, listview.getHeight());
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
		if (item.getTitle().toString().equalsIgnoreCase("Refresh")) {
			Utility.displayErrorConnection(this);
			try {
				getRelatedImages(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void getRelatedImages(boolean isClearCache) {
		ParseQuery query = new ParseQuery("Image");
		query.include("user");
		query.orderByDescending("createdAt");

		if (isClearCache) {
			query.clearCachedResult();
			ha.getArray().clear();
			ListView listview = (ListView) findViewById(R.id.homeTable);
			listview.setAdapter(ha);
		}
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> images, ParseException e) {
				if (e == null) {
					final ArrayList<HomePost> array = new ArrayList<HomePost>();
					int i = 0;
					for (ParseObject po : images) {
						if (i < num_image) {
							if (ParseUser.getCurrentUser() != null) {
								if (po.getParseUser("user")
										.getObjectId()
										.equals(ParseUser.getCurrentUser()
												.getObjectId())) {
									loadImage(po, array, i);
								} else {
									if (!checkFriend(po, array, i))
										i--;
								}
								i++;
							} else {
								break;
							}
						} else {
							break;
						}
					}
					ha.setArray(array);
				} else {
				}
			}
		});
	}

	private boolean checkFriend(ParseObject po, ArrayList<HomePost> arr, int i) {
		ParseUser pu = po.getParseUser("user");
		SQLiteDatabase myDb = openOrCreateDatabase("nish_user.db",
				Context.MODE_PRIVATE, null);
		myDb.execSQL("create table if not exists friend ( _id integer primary key autoincrement, friendId text not null);");
		Cursor user = myDb.query("friend", null, null, null, null, null, null);
		user.moveToFirst();
		int count = 1;
		while (user.isAfterLast() == false) {
			String objId = user.getString(1);
			if (pu.getObjectId().equals(objId)) {
				loadImage(po, arr, i);
				myDb.close();
				return true;
			}
			count++;
			user.moveToNext();
		}
		myDb.close();
		return false;
	}

	private void loadImage(ParseObject po, ArrayList<HomePost> arr, int i) {
		final ArrayList<HomePost> array = arr;
		final ParseUser u = po.getParseUser("user");
		ParseFile pf = (ParseFile) po.get("imageFile");
		final String location = po.getString("location");
		final int count = i;
		final ParseObject a = po;
		arr.add(new HomePost());
		ListView listview = (ListView) findViewById(R.id.homeTable);
		listview.setAdapter(ha);
		pf.getDataInBackground(new GetDataCallback() {

			@Override
			public void done(byte[] data, ParseException e) {
				if (e == null) {
					if (array.size() > count) {
						HomePost hp = array.get(count);
						List<String> arrs = a.getList("like");
						if (arrs == null) {
							arrs = new ArrayList<String>();
						}
						hp.setLocation(location);
						hp.setLike(arrs);
						hp.setUser(u);
						hp.setImage(data);
						hp.setDate(dateDifference(a));
						hp.setPo(a);
						ha.notifyDataSetChanged();
					}
				} else {
					e.printStackTrace();
				}
			}
		});
	}

	private String dateDifference(ParseObject po) {
		Calendar cal = Calendar.getInstance();
		Date currDate = cal.getTime();
		Date objDate = po.getCreatedAt();
		long secondDiff = (currDate.getTime() - objDate.getTime()) / 1000;

		if (secondDiff < 0)
			return 0 + "s ago";
		else if (secondDiff < 60)
			return secondDiff + "s ago";
		else if (secondDiff < 60 * 60)
			return (int) secondDiff / 60 + "m ago";
		else if (secondDiff < 60 * 60 * 24)
			return (int) secondDiff / (60 * 60) + "h ago";
		else if (secondDiff < 60 * 60 * 24 * 7)
			return (int) secondDiff / (60 * 60 * 24) + "d ago";
		else if (secondDiff < 60 * 60 * 24 * 7 * 4)
			return (int) secondDiff / (60 * 60 * 24 * 7) + "w ago";
		else if (secondDiff < 60 * 60 * 24 * 7 * 4 * 12)
			return (int) secondDiff / (60 * 60 * 24 * 7 * 4) + "M ago";
		else
			return "more than 1y";
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}