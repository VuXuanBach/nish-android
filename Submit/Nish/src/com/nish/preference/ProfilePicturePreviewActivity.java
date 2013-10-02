package com.nish.preference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.MainActivity;
import com.nish.R;
import com.nish.RegisterActivity;
import com.nish.filter.BitmapFilter;
import com.nish.model.Utility;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ProfilePicturePreviewActivity extends SherlockActivity implements
		OnClickListener {

	private File yourDir;
	private ImageView imgView;
	private Button grayButton, vagueButton, reliefButton, oilButton,
			neonButton, pixelateButton, invertButton, tvButton, oldButton,
			sharpenButton, lightButton, lomoButton, sourceButton;
	private Bitmap sourceBitmap, change;
	private Handler handler;
	private ProgressDialog progressDialog = null;
	private int styleId = 0;
	private String phase;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			setRequestedOrientation(Utility.NOSENSOR);
			setContentView(R.layout.activity_camera_preview);
			setTheme(Utility.THEME);
			Bundle bundle = this.getIntent().getExtras();
			phase = bundle.getString("phase");

			loadImageFromSDCard();

			File fileResize = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/Nish/avatar.jpg");
			change = sourceBitmap = Utility.decodeFile(fileResize, 50);

			grayButton = (Button) findViewById(R.id.button_change_to_gray);
			vagueButton = (Button) findViewById(R.id.button_change_to_vague);
			reliefButton = (Button) findViewById(R.id.button_change_to_relief);
			oilButton = (Button) findViewById(R.id.button_change_to_oid);
			neonButton = (Button) findViewById(R.id.button_change_to_neon);
			pixelateButton = (Button) findViewById(R.id.button_change_to_pixelate);
			invertButton = (Button) findViewById(R.id.button_change_to_invert);
			tvButton = (Button) findViewById(R.id.button_change_to_tv);
			oldButton = (Button) findViewById(R.id.button_change_to_old);
			sharpenButton = (Button) findViewById(R.id.button_change_to_sharpen);
			lightButton = (Button) findViewById(R.id.button_change_to_light);
			lomoButton = (Button) findViewById(R.id.button_change_to_lomo);
			sourceButton = (Button) findViewById(R.id.button_change_to_source);

			grayButton.setOnClickListener(this);
			vagueButton.setOnClickListener(this);
			reliefButton.setOnClickListener(this);
			oilButton.setOnClickListener(this);
			neonButton.setOnClickListener(this);
			pixelateButton.setOnClickListener(this);
			invertButton.setOnClickListener(this);
			tvButton.setOnClickListener(this);
			oldButton.setOnClickListener(this);
			sharpenButton.setOnClickListener(this);
			lightButton.setOnClickListener(this);
			lomoButton.setOnClickListener(this);
			sourceButton.setOnClickListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadImageFromSDCard() {
		yourDir = new File(Environment.getExternalStorageDirectory(),
				"/Nish/avatar.jpg");
		imgView = (ImageView) findViewById(R.id.img_preview);
		imgView.setImageBitmap(BitmapFactory.decodeFile(yourDir
				.getAbsolutePath()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("OK").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("BACK").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			if (item.getTitle().toString().equalsIgnoreCase("OK")) {
				if (phase.equals("pref")) {
					uploadImage();
				} else {
					File dir = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/Nish");
					dir.mkdirs();
					File file = new File(dir, "avatar.jpg");
					try {
						FileOutputStream out = new FileOutputStream(file);
						change.compress(Bitmap.CompressFormat.JPEG, 100, out);
						out.flush();
						out.close();
						RegisterActivity.ivphoto.setImageBitmap(change);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				finish();
			}
			if (item.getTitle().toString().equalsIgnoreCase("BACK")) {
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void uploadImage() {
		Utility.displayErrorConnection(this);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			change.compress(Bitmap.CompressFormat.JPEG, 100, baos);

			byte[] byte_img_data = baos.toByteArray();
			final ParseFile file = new ParseFile("avatar.jpg", byte_img_data);
			file.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					if (e == null) {
						if (ParseUser.getCurrentUser() != null) {
							ParseUser pu = ParseUser.getCurrentUser();
							pu.put("avatar", file);
							pu.saveInBackground();
						}
					} else {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		try {
			styleId = 0;
			String title = "";
			switch (v.getId()) {
			case R.id.button_change_to_gray:
				styleId = BitmapFilter.GRAY_STYLE;
				title = "Gray";
				break;
			case R.id.button_change_to_oid:
				styleId = BitmapFilter.OIL_STYLE;
				title = "Oil";
				break;
			case R.id.button_change_to_relief:
				styleId = BitmapFilter.RELIEF_STYLE;
				title = "Relief";
				break;
			case R.id.button_change_to_vague:
				styleId = BitmapFilter.VAGUE_STYLE;
				title = "Vague";
				break;
			case R.id.button_change_to_neon:
				styleId = BitmapFilter.NEON_STYLE;
				title = "Neon";
				break;
			case R.id.button_change_to_pixelate:
				styleId = BitmapFilter.PIXELATE_STYLE;
				title = "Pixel Late";
				break;
			case R.id.button_change_to_invert:
				styleId = BitmapFilter.INVERT_STYLE;
				title = "Invert";
				break;
			case R.id.button_change_to_tv:
				styleId = BitmapFilter.TV_STYLE;
				title = "TV";
				break;
			case R.id.button_change_to_old:
				styleId = BitmapFilter.OLD_STYLE;
				title = "Old";
				break;
			case R.id.button_change_to_sharpen:
				styleId = BitmapFilter.SHARPEN_STYLE;
				title = "Sharpen";
				break;
			case R.id.button_change_to_light:
				styleId = BitmapFilter.LIGHT_STYLE;
				title = "Light";
				break;
			case R.id.button_change_to_lomo:
				styleId = BitmapFilter.LOMO_STYLE;
				title = "Lomo";
				break;
			case R.id.button_change_to_source:
				imgView.setImageBitmap(sourceBitmap);
				change = sourceBitmap;
				title = "Original";
				break;
			}

			progressDialog = ProgressDialog.show(this, title, "Processing...");
			new Thread() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					change = BitmapFilter.changeStyle(sourceBitmap, styleId);
					Message msg = Message.obtain();
					msg.what = 1;
					handler.sendMessage(msg);
				}
			}.start();

			handler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					imgView.setImageBitmap(change);
					progressDialog.dismiss();
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}
