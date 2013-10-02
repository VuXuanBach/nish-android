package com.nish.preference;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.R;
import com.nish.model.Utility;
import com.parse.Parse;

public class AboutActivity extends SherlockActivity {

	protected void onCreate(Bundle savedInstanceState) {
		// TODOAuto-generated method stub
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		Utility.displayErrorConnection(this);
		setRequestedOrientation(Utility.NOSENSOR);
		setContentView(R.layout.activity_about);
		setTheme(Utility.THEME);
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
}
