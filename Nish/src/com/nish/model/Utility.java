package com.nish.model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.R;

public class Utility {
	public static int THEME = R.style.Theme_Sherlock;
	public static final int NOSENSOR = 5;
	public static final Pattern EMAIL_PATTERN = Pattern
			.compile("[a-zA-Z0-9+._%-+]{1,256}" + "@"
					+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,64}" + "(" + "."
					+ "[a-zA-Z0-9][a-zA-Z0-9-]{0,25}" + ")+");
	public static final int MAX_FILE_SIZE = Integer.MAX_VALUE;

	public static boolean isOnline(Context c) {
		ConnectivityManager cm = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static boolean validateEmail(String email) {
		return Utility.EMAIL_PATTERN.matcher(email).matches();
	}

	public static boolean validateNullField(String str) {
		return (str == null || str.length() == 0) ? false : true;
	}

	public static void displayDialog(Context context, String title,
			String message) {
		new AlertDialog.Builder(context)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton("Dismiss",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();
	}

	public static boolean displayErrorConnection(Context co) {
		if (!Utility.isOnline(co)) {
			Toast.makeText(co, "Couldn't connect", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	public static void unbindDrawables(View view) {
		try {
			if (view.getBackground() != null) {
				view.getBackground().setCallback(null);
			}
			if (view instanceof ViewGroup) {
				for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
					unbindDrawables(((ViewGroup) view).getChildAt(i));
				}
				((ViewGroup) view).removeAllViews();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean checkCurrentAndroidVersion() {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return false;
		}
		return true;
	}

	public static byte[] convertFileToBytes(String path) {
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] byte_img_data = baos.toByteArray();

		return byte_img_data;
	}

	public static Bitmap decodeFile(File f, int size) {
		try {
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			final int REQUIRED_SIZE = size;
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_SIZE
					&& o.outHeight / scale / 2 >= REQUIRED_SIZE)
				scale *= 2;

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
