package com.nish.preference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.nish.model.CropOption;
import com.nish.model.CropOptionAdapter;
import com.nish.model.Utility;
import com.parse.Parse;

public class ProfilePictureActivity extends SherlockActivity {

	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	private String phase;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			setRequestedOrientation(Utility.NOSENSOR);
			Bundle bundle = this.getIntent().getExtras();
			String type = bundle.getString("type");
			phase = bundle.getString("phase");

			if (type.equalsIgnoreCase("take")) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				File dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/Nish");
				dir.mkdirs();
				File file = new File(dir, "avatar.jpg");
				mImageCaptureUri = Uri.fromFile(file);
				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
						mImageCaptureUri);

				intent.putExtra("return-data", true);
				startActivityForResult(intent, PICK_FROM_CAMERA);
			}
			if (type.equalsIgnoreCase("choose")) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);

				startActivityForResult(
						Intent.createChooser(intent, "Complete action using"),
						PICK_FROM_FILE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();
			break;
		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();
			doCrop();
			break;
		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				File dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/Nish");
				dir.mkdirs();
				File file = new File(dir, "avatar.jpg");
				try {
					FileOutputStream out = new FileOutputStream(file);
					photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
				} catch (Exception e) {
				}

				Bitmap com = Utility.decodeFile(file, 50);
				try {
					FileOutputStream out = new FileOutputStream(file);
					com.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
				} catch (Exception e) {
				}
			}
			Bundle sendBundle = new Bundle();
			sendBundle.putString("phase", phase);
			Intent myIntent = new Intent(ProfilePictureActivity.this,
					ProfilePicturePreviewActivity.class);
			myIntent.putExtras(sendBundle);
			startActivityForResult(myIntent, 1);
			finish();
			break;
		}
	}

	private void doCrop() {
		try {
			Utility.displayErrorConnection(this);
			final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

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
				intent.setData(mImageCaptureUri);

				intent.putExtra("outputX", 200);
				intent.putExtra("outputY", 200);
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("scale", true);
				intent.putExtra("return-data", true);

				if (size == 1) {
					ResolveInfo res = list.get(0);
					intent.setComponent(new ComponentName(
							res.activityInfo.packageName, res.activityInfo.name));
					startActivityForResult(intent, CROP_FROM_CAMERA);
				} else {
					for (ResolveInfo res : list) {
						final CropOption co = new CropOption();

						co.title = getPackageManager().getApplicationLabel(
								res.activityInfo.applicationInfo);
						co.icon = getPackageManager().getApplicationIcon(
								res.activityInfo.applicationInfo);
						co.appIntent = new Intent(intent);

						co.appIntent.setComponent(new ComponentName(
								res.activityInfo.packageName,
								res.activityInfo.name));

						cropOptions.add(co);
					}

					CropOptionAdapter adapter = new CropOptionAdapter(
							getApplicationContext(), cropOptions);

					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Choose Crop App");
					builder.setAdapter(adapter,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int item) {
									startActivityForResult(
											cropOptions.get(item).appIntent,
											CROP_FROM_CAMERA);
								}
							});

					builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {

							if (mImageCaptureUri != null) {
								getContentResolver().delete(mImageCaptureUri,
										null, null);
								mImageCaptureUri = null;
							}
						}
					});

					AlertDialog alert = builder.create();
					alert.show();
				}
			}
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
