package com.nish.preference;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.R;
import com.nish.model.Utility;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class EditProfileActivity extends SherlockActivity {

	private boolean isEmail;
	private Context context;

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
			setContentView(R.layout.activity_edit_profile);
			setTheme(Utility.THEME);
			context = this;
			getData();
			Button submit = (Button) findViewById(R.id.submitProfile);
			submit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					submit();
				}
			});
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
		try {
			if (item.getTitle().toString().equalsIgnoreCase("back")) {
				getData();
				finish();
			}
			if (item.getTitle().toString().equalsIgnoreCase("refresh")) {
				Utility.displayErrorConnection(this);
				getData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void getData() {
		EditText first = (EditText) findViewById(R.id.firstName);
		EditText last = (EditText) findViewById(R.id.lastName);
		EditText email = (EditText) findViewById(R.id.emailProfile);
		EditText phone = (EditText) findViewById(R.id.phoneProfile);

		ParseUser pu = ParseUser.getCurrentUser();
		if (pu != null) {
			first.setText(pu.getString("firstname"));
			last.setText(pu.getString("lastname"));
			email.setText(pu.getString("email"));
			phone.setText(pu.getString("phone"));
		}
	}

	private void submit() {
		if (ParseUser.getCurrentUser() != null) {
			ParseUser user = ParseUser.getCurrentUser();

			EditText tvFirst = (EditText) findViewById(R.id.firstName);
			EditText tvLast = (EditText) findViewById(R.id.lastName);
			EditText tvEmail = (EditText) findViewById(R.id.emailProfile);
			EditText tvPhone = (EditText) findViewById(R.id.phoneProfile);

			String email = tvEmail.getText().toString();
			String first = tvFirst.getText().toString();
			String last = tvLast.getText().toString();
			String phone = tvPhone.getText().toString();

			if (!Utility.validateEmail(email)) {
				tvEmail.setError("Please enter valid email address");
				isEmail = false;
			} else {
				tvEmail.setError(null);
				isEmail = true;
			}
			if (!Utility.validateNullField(first)) {
				first = "";
			}
			if (!Utility.validateNullField(last)) {
				last = "";
			}
			if (!Utility.validateNullField(phone)) {
				phone = "";
			}

			if (isEmail) {
				if (!Utility.isOnline(this)) {
					Utility.displayDialog(context, "Error",
							"No Internet access");
				} else {
					user.setEmail(email);
					user.put("firstname", first);
					user.put("lastname", last);
					user.put("phone", phone);
					final ProgressDialog pdialog = ProgressDialog.show(this,
							"", "Submitting ...", true);
					pdialog.getWindow().setLayout(300, 300);
					user.saveInBackground(new SaveCallback() {

						@Override
						public void done(ParseException e) {
							// TODO Auto-generated method stub
							if (e == null) {
								pdialog.dismiss();
							} else {
								pdialog.dismiss();
								Utility.displayDialog(context, "Error",
										"This email is already been used");
							}
						}
					});
				}
			}
		}
	}
}