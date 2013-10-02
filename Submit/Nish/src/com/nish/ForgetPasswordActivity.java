package com.nish;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.model.Utility;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class ForgetPasswordActivity extends SherlockActivity {

	private boolean isEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			setRequestedOrientation(Utility.NOSENSOR);
			if (Utility.checkCurrentAndroidVersion())
				requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_forget_password);
			setTheme(Utility.THEME);

			Button send = (Button) findViewById(R.id.btn_send);
			send.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					send();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void send() {
		Utility.displayErrorConnection(this);
		TextView tvEmail = (TextView) findViewById(R.id.txt_email_reset);
		String email = tvEmail.getText().toString();

		if (!Utility.validateEmail(email)) {
			tvEmail.setError("Please enter valid email address");
			isEmail = false;
		} else {
			tvEmail.setError(null);
			isEmail = true;
		}

		if (isEmail) {
			if (!Utility.isOnline(this)) {
				Utility.displayDialog(this, "Error", "No Internet access");
			} else {
				final ProgressDialog pdialog = ProgressDialog.show(this, "",
						"Sending ...", true);
				pdialog.getWindow().setLayout(300, 300);
				ParseUser.requestPasswordResetInBackground(email,
						new RequestPasswordResetCallback() {
							public void done(ParseException e) {
								pdialog.dismiss();
								if (e == null) {
									Utility.displayDialog(
											ForgetPasswordActivity.this,
											"Info", "Sending success");
								} else {
									Utility.displayDialog(
											ForgetPasswordActivity.this,
											"Error", "Invalid email");
									e.printStackTrace();
								}
							}
						});
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
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
			TextView tvEmail = (TextView) findViewById(R.id.txt_email_reset);
			tvEmail.setText("");
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}
