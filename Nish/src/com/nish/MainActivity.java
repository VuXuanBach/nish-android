package com.nish;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;

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

public class MainActivity extends SherlockActivity {

	private ImageAdapter ia;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			Utility.displayErrorConnection(this);
			setRequestedOrientation(Utility.NOSENSOR);
			if (Utility.checkCurrentAndroidVersion())
				requestWindowFeature(Window.FEATURE_NO_TITLE);

			if (ParseUser.getCurrentUser() == null) {
				setContentView(R.layout.activity_main);
				ParseQuery.clearAllCachedResults();
				ia = new ImageAdapter(this);
				getAllPublicImages(false);

				Button login = (Button) findViewById(R.id.login);
				login.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent myIntent = new Intent(MainActivity.this,
								LoginActivity.class);
						startActivity(myIntent);
						finish();
					}
				});

				Button signup = (Button) findViewById(R.id.signup);
				signup.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Create intent and bundle to attach a string
						Intent myIntent = new Intent(MainActivity.this,
								RegisterActivity.class);
						startActivity(myIntent);
						finish();
					}
				});
			} else {
				Intent myIntent = new Intent(MainActivity.this,
						TabMainActivity.class);
				startActivity(myIntent);
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Refresh").setIcon(R.drawable.ic_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
		query.whereEqualTo("isPublic", true);
		query.setLimit(15);
		query.orderByDescending("createdAt");
		query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
		if (isClearCache) {
			query.clearCachedResult();
			ia.getArray().clear();
			GridView gridview = (GridView) findViewById(com.nish.R.id.mainGrid);
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
					GridView gridview = (GridView) findViewById(com.nish.R.id.mainGrid);
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
