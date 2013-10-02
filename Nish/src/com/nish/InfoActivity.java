package com.nish;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.model.ImageAdapter;
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

public class InfoActivity extends SherlockActivity {
	private ImageAdapter ia;
	public static Handler h;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			setRequestedOrientation(Utility.NOSENSOR);
			setContentView(R.layout.activity_info);
			setTheme(Utility.THEME);
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
			ia = new ImageAdapter(this);
			if (ParseUser.getCurrentUser() != null) {
				getCurrentUserImages(false);
				ParseUser pu = ParseUser.getCurrentUser();
				TextView tv = (TextView) findViewById(R.id.usernameInfo);
				tv.setText(pu.getUsername());
				getAvatar(pu);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getAvatar(ParseUser u) {
		ParseFile pf = (ParseFile) u.get("avatar");
		if (pf != null) {
			pf.getDataInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] data, ParseException e) {
					ImageView iv = (ImageView) findViewById(R.id.avatarInfo);
					if (e == null) {
						Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,
								data.length);
						iv.setImageBitmap(bmp);
					} else {
						iv.setImageResource(R.drawable.default_avatar);
					}
				}
			});
		} else {
			ImageView iv = (ImageView) findViewById(R.id.avatarInfo);
			iv.setImageResource(R.drawable.default_avatar);
		}
	}

	private void getCurrentUserImages(boolean isClearCache) {
		ParseQuery query = new ParseQuery("Image");
		if (ParseUser.getCurrentUser() != null) {
			query.whereEqualTo("user", ParseUser.getCurrentUser());
			query.orderByDescending("createdAt");
			query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
			if (isClearCache) {
				query.clearCachedResult();
				ia.getArray().clear();
				GridView gridview = (GridView) findViewById(R.id.infoTable);
				gridview.setAdapter(ia);
			}
			
			query.findInBackground(new FindCallback() {
				public void done(List<ParseObject> images, ParseException e) {
					if (e == null) {
						final ArrayList<byte[]> array = new ArrayList<byte[]>();
						for (int j = 0; j < images.size(); j++) {
							byte[] b = new byte[1];
							array.add(b);
						}
						int i = 0;
						GridView gridview = (GridView) findViewById(com.nish.R.id.infoTable);
						gridview.setAdapter(ia);
						for (ParseObject po : images) {
							ParseFile pf = (ParseFile) po.get("imageFile");
							final int count = i;
							i++;
							pf.getDataInBackground(new GetDataCallback() {

								public void done(byte[] data, ParseException e) {
									if (e == null) {
										if (array.size() > count) {
											Log.d("DONEEEEEEEEEEEEEEEEEEE", "aaaaaaaaaaaaaaaa");
											array.set(count, data);
											ia.notifyDataSetChanged();
										}
									} else {
										e.printStackTrace();
									}
								}
							});
						}
						Log.d("DONEEEEEEEEEEEEEEEEEEE", "ccccccccccccccccccccccc");
						ia.setArray(array);
					} else {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Back").setIcon(R.drawable.ic_prev)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add("Refresh").setIcon(R.drawable.ic_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add("Setting").setIcon(R.drawable.ic_setting)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
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
				if (ParseUser.getCurrentUser() != null) {
					getCurrentUserImages(true);
					ParseUser pu = ParseUser.getCurrentUser();
					getAvatar(pu);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (item.getTitle().toString().equalsIgnoreCase("setting")) {
			Intent myIntent = new Intent(this, PreferencesActivity.class);
			startActivity(myIntent);

		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}