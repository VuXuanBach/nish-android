package com.nish;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Toolbar extends LinearLayout {

	public Toolbar(final Context context) {
		super(context);
	}

	public Toolbar(final Context con, AttributeSet attrs) {
		super(con, attrs);
		try {
			setOrientation(HORIZONTAL);
			setBackgroundColor(getResources().getColor(
					android.R.color.transparent));

			LayoutInflater inflater = (LayoutInflater) con
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			inflater.inflate(R.layout.navigation, this);

			TypedArray a = con.obtainStyledAttributes(attrs,
					R.styleable.Toolbar);
			String option = a.getString(R.styleable.Toolbar_tab_id);

			String resourceId = "com.nish:id/" + option;
			int optionId = getResources().getIdentifier(resourceId, null, null);
			TextView currentOption = (TextView) findViewById(optionId);
			currentOption.setTextColor(getResources().getColor(
					android.R.color.black));
			currentOption.requestFocus(optionId);
			currentOption.setFocusable(false);
			currentOption.setClickable(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
