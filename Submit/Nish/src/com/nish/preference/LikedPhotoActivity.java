package com.nish.preference;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.R;
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

public class LikedPhotoActivity extends SherlockActivity {
	private ImageAdapter ia;

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
			setContentView(R.layout.activity_explore);
			setTheme(Utility.THEME);

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
			try {
				Utility.displayErrorConnection(this);
				getAllPublicImages(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void getAllPublicImages(boolean isClearCache) {
		ParseQuery query = new ParseQuery("Image");
		if (ParseUser.getCurrentUser() != null) {
			query.whereEqualTo("like", ParseUser.getCurrentUser().getUsername());
			query.setLimit(18);
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
						Log.d("SIZE", images.size() + "");
						for (int j = 0; j < images.size(); j++) {
							byte[] b = new byte[1];
							array.add(b);
						}
						int i = 0;
						GridView gridview = (GridView) findViewById(com.nish.R.id.exploreGrid);
						gridview.setAdapter(ia);
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
									}
								}
							});
						}
						ia.setArray(array);
					} else {
					}
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}