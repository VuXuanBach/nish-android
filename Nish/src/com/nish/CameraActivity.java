package com.nish;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.nish.model.CustomSAXParser;
import com.nish.model.GPSTracker;
import com.nish.model.Utility;
import com.parse.Parse;
import com.parse.ParseUser;

public class CameraActivity extends Activity {
	private Intent cameraIntent;
	private Uri mImageCaptureUri;
	private GPSTracker gps;
	private static final int PICK_FROM_CAMERA = 9, CROP_FROM_CAMERA = 10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			try {
				Parse.initialize(this,
						"PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
						"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
			} catch (Exception e) {
			}
			setRequestedOrientation(Utility.NOSENSOR);
			if(ParseUser.getCurrentUser() != null) {
				boolean lo = ParseUser.getCurrentUser().getBoolean("locationPrivacy");
				if(lo) {
					Toast.makeText(this, "Location enable", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, "Location disable", Toast.LENGTH_SHORT).show();
				}
			}
			cameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent
					.putExtra(
							android.provider.MediaStore.EXTRA_OUTPUT,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

			File dir = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/Nish");
			dir.mkdirs();
			File file = new File(dir, "temp.jpg");
			mImageCaptureUri = Uri.fromFile(file);
			cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
					mImageCaptureUri);

			cameraIntent.putExtra("return-data", true);
			startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (cameraIntent != null) {
			if (resultCode == 0) {
				Intent myIntent = new Intent(CameraActivity.this,
						TabMainActivity.class);
				startActivityForResult(myIntent, 1);
				finish();
			}
			if (requestCode == PICK_FROM_CAMERA && resultCode == -1) {
				doCrop();
			}
			if (requestCode == CROP_FROM_CAMERA && resultCode == -1) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					File dir = new File(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ "/Nish");
					dir.mkdirs();
					File file = new File(dir, "temp.jpg");
					try {
						FileOutputStream out = new FileOutputStream(file);
						photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
						out.flush();
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					Bitmap com = Utility.decodeFile(file, 250);
					try {
						FileOutputStream out = new FileOutputStream(file);
						com.compress(Bitmap.CompressFormat.JPEG, 100, out);
						out.flush();
						out.close();
					} catch (Exception e) {
					}
				}
				userSqlite();
				Intent myIntent = new Intent(CameraActivity.this,
						CameraPreviewActivity.class);
				startActivityForResult(myIntent, 1);
				finish();
			}
		}
	}

	private void doCrop() {
		try {
			Utility.displayErrorConnection(this);

			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setType("image/*");

			List<ResolveInfo> list = getPackageManager().queryIntentActivities(
					intent, 0);
			int size = list.size();
			if (size == 0) {
				Toast.makeText(this, "Can not find image crop app",
						Toast.LENGTH_SHORT).show();
				return;
			} else {
				Log.d("CAMERA", "size > 0");
				intent.setData(mImageCaptureUri);

				intent.putExtra("outputX", 200);
				intent.putExtra("outputY", 200);
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("scale", true);
				intent.putExtra("return-data", true);

				if (size == 1) {
					// Intent i = new Intent(intent);
					ResolveInfo res = list.get(0);
					intent.setComponent(new ComponentName(
							res.activityInfo.packageName, res.activityInfo.name));
					startActivityForResult(intent, CROP_FROM_CAMERA);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void userSqlite() {
		try {
			SQLiteDatabase myDb = openOrCreateDatabase("nish_user.db",
					Context.MODE_PRIVATE, null);
			myDb.execSQL("drop table if exists location");
			myDb.execSQL("create table location ( _id integer primary key autoincrement, address text not null);");
			ContentValues newValues = new ContentValues();
			String loc = (ParseUser.getCurrentUser()
					.getBoolean("locationPrivacy")) ? getLocation() : "";
			String lo = (loc.length() >= 2) ? loc
					.substring(1, loc.length() - 1) : "";
			newValues.put("address", lo.equals("") ? "N/A" : lo);
			myDb.insert("location", null, newValues);
			myDb.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getLocation() {
		gps = new GPSTracker(this);
		if (gps.canGetLocation()) {
			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();
			final String url = "http://maps.googleapis.com/maps/api/geocode/xml?latlng="
					+ latitude + "," + longitude + "&sensor=true";
			String result = execGet(url);
			String address = parseResult(result);
			return address;
		} else {
			gps.showSettingsAlert();
			return "N/A";
		}
	}

	private String execGet(String url) {
		String ret = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpParams params = new BasicHttpParams();
			get.setParams(params);
			BasicResponseHandler handler = new BasicResponseHandler();
			ret = httpClient.execute(get, handler);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	private String parseResult(String raw) {
		String ret = "";
		SAXParserFactory spf = SAXParserFactory.newInstance();
		// get a new instance of parser
		SAXParser sp;
		try {
			sp = spf.newSAXParser();
			CustomSAXParser myHandler = new CustomSAXParser();
			sp.parse(new InputSource(new StringReader(raw)), myHandler);
			ret = myHandler.getLocations().toString();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}