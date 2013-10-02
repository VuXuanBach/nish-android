package com.nish;

import com.nish.model.Utility;
import com.parse.Parse;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class TabMainActivity extends TabActivity implements OnTabChangeListener {

	public static TabSpec homespec, explorespec, cameraspec, newspec, infospec;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			if (Utility.checkCurrentAndroidVersion())
				requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_tabhost_main);

			final TabHost tabHost = getTabHost();

			homespec = tabHost.newTabSpec("Homes");
			homespec.setIndicator("",
					getResources().getDrawable(R.drawable.icon_home_tab));
			Intent homeIntent = new Intent(TabMainActivity.this,
					HomeActivity.class);
			homespec.setContent(homeIntent);
			
			explorespec = tabHost.newTabSpec("Explore");
			explorespec.setIndicator("",
					getResources().getDrawable(R.drawable.icon_star_tab));
			Intent exploreIntent = new Intent(TabMainActivity.this,
					ExploreActivity.class);
			explorespec.setContent(exploreIntent);

			cameraspec = tabHost.newTabSpec("Camera");
			cameraspec.setIndicator("",
					getResources().getDrawable(R.drawable.icon_camera_tab));
			Intent cameraIntent = new Intent(TabMainActivity.this,
					CameraActivity.class);
			cameraspec.setContent(cameraIntent);

			newspec = tabHost.newTabSpec("News");
			newspec.setIndicator("",
					getResources().getDrawable(R.drawable.icon_heart_tab));
			Intent newsIntent = new Intent(TabMainActivity.this,
					FriendActivity.class);
			newspec.setContent(newsIntent);
			tabHost.setOnTabChangedListener(new OnTabChangeListener(){    
			    public void onTabChanged(String tabID) {    
			    	tabHost.clearFocus(); 
			    }   
			}); 
			infospec = tabHost.newTabSpec("Info");
			infospec.setIndicator("",
					getResources().getDrawable(R.drawable.icon_perinfo_tab));
			Intent infoIntent = new Intent(TabMainActivity.this,
					InfoActivity.class);
			infospec.setContent(infoIntent);

			tabHost.addTab(homespec);
			tabHost.addTab(explorespec);
			tabHost.addTab(cameraspec);
			tabHost.addTab(newspec);
			tabHost.addTab(infospec);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		if (tabId.equalsIgnoreCase("Homes")) {
			Intent intent = new Intent(TabMainActivity.this, HomeActivity.class);
			homespec.setContent(intent);
		}
		if (tabId.equalsIgnoreCase("Explore")) {
			Intent intent = new Intent(TabMainActivity.this,
					ExploreActivity.class);
			explorespec.setContent(intent);
		}
		if (tabId.equalsIgnoreCase("Camera")) {
			Intent intent = new Intent(TabMainActivity.this,
					CameraActivity.class);
			cameraspec.setContent(intent);
		}
		if (tabId.equalsIgnoreCase("News")) {
			Intent intent = new Intent(TabMainActivity.this,
					FriendActivity.class);
			newspec.setContent(intent);
		}
		if (tabId.equalsIgnoreCase("Info")) {
			Intent intent = new Intent(TabMainActivity.this,
					PreferencesActivity.class);
			infospec.setContent(intent);
		}
	}
}
