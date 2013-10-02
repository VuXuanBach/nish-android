package com.nish;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.GridView;
import android.widget.ListView;

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
import com.parse.ProgressCallback;

public class ExploreActivity extends SherlockActivity {

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
			setContentView(R.layout.activity_explore);
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
			getAllPublicImages(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
				getAllPublicImages(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void getAllPublicImages(boolean isClearCache) {
		ParseQuery query = new ParseQuery("Image");
		query.whereEqualTo("isPublic", true);
		query.setLimit(15);
		query.orderByDescending("createdAt");
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		if (isClearCache) {
			query.clearCachedResult();
			ia.getArray().clear();
			GridView gridview = (GridView) findViewById(com.nish.R.id.exploreGrid);
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
					GridView listview = (GridView) findViewById(R.id.exploreGrid);
					listview.setAdapter(ia);
					for (ParseObject po : images) {
						ParseFile pf = (ParseFile) po.get("imageFile");
						final int count = i;
						i++;
						pf.getDataInBackground(new GetDataCallback() {

							public void done(byte[] data, ParseException e) {
								if (e == null) {
									if (array.size() > count) {
										array.set(count, data);
										ia.notifyDataSetChanged();
									}
								} else {
									e.printStackTrace();
								}
							}
						});
					}
					ia.setArray(array);
				} else {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}