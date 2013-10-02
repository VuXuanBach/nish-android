package com.nish;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.filter.BitmapFilter;
import com.nish.model.Utility;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class CameraPreviewActivity extends SherlockActivity implements
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

	protected void onCreate(Bundle savedInstanceState) {
		// TODOAuto-generated method stub
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

			loadImageFromSDCard();

			File fileResize = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/Nish/temp.jpg");
			sourceBitmap = Utility.decodeFile(fileResize, 250);
			change = sourceBitmap;

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
				"/Nish/temp.jpg");
		imgView = (ImageView) findViewById(R.id.img_preview);
		imgView.setImageBitmap(BitmapFactory.decodeFile(yourDir
				.getAbsolutePath()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("PUBLISH").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add("BACK").setShowAsAction(
				MenuItem.SHOW_AS_ACTION_IF_ROOM
						| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("PUBLISH")) {
			new AlertDialog.Builder(this)
					.setMessage("Let the World see it!")
					.setPositiveButton("Agree",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									uploadImage(true);
									Intent myIntent = new Intent(
											CameraPreviewActivity.this,
											MainActivity.class);
									startActivityForResult(myIntent, 1);
									finish();
								}
							})
					.setNegativeButton("No thanks",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									uploadImage(false);
									Intent myIntent = new Intent(
											CameraPreviewActivity.this,
											MainActivity.class);
									startActivityForResult(myIntent, 1);
									finish();
								}
							}).show();

		}
		if (item.getTitle().toString().equalsIgnoreCase("BACK")) {
			Intent myIntent = new Intent(CameraPreviewActivity.this,
					TabMainActivity.class);
			startActivityForResult(myIntent, 1);
			finish();
		}
		return false;
	}

	private void uploadImage(boolean isPublic) {
		try {
			Utility.displayErrorConnection(this);
			final boolean isPu = isPublic;
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				change.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				saveFinalImage(change);

				byte[] byte_img_data = baos.toByteArray();
				final ParseFile file = new ParseFile("picture.jpg",
						byte_img_data);
				file.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						if (e == null) {
							if (ParseUser.getCurrentUser() != null) {
								ParseObject imageObj = new ParseObject("Image");
								imageObj.put("user", ParseUser.getCurrentUser());
								imageObj.put("imageFile", file);
								imageObj.put("isPublic", isPu);
								imageObj.put("location", getAddressSqlite());
								imageObj.saveInBackground();
							}
						} else {
							e.printStackTrace();
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveFinalImage(Bitmap c) {
		File dir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath());

		File file = new File(dir, "pic" + System.currentTimeMillis() + ".jpg");
		try {
			FileOutputStream out = new FileOutputStream(file);
			c.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
		}
	}

	private String getAddressSqlite() {
		SQLiteDatabase myDb = openOrCreateDatabase("nish_user.db",
				Context.MODE_PRIVATE, null);
		myDb.execSQL("create table if not exists location ( _id integer primary key autoincrement, address text not null);");
		Cursor location = myDb.query("location", null, null, null, null, null,
				null);
		if (location.moveToFirst()) {
			myDb.close();
			return location.getString(1);
		}
		myDb.close();
		return "N/A";
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
					Log.d("STYLE", styleId + "");
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