package com.nish.model;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nish.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class FriendAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<FriendRow> array;

	public ArrayList<FriendRow> getArray() {
		return array;
	}

	public void setArray(ArrayList<FriendRow> array) {
		this.array = array;
	}

	public FriendAdapter(Context c) {
		mContext = c;
		array = new ArrayList<FriendRow>();
	}

	public int getCount() {
		return array.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View row = inflater.inflate(R.layout.custom_friend, null);
		final FriendRow hp = array.get(position);
		final int index = position;
		// Set Name
		TextView name = (TextView) row.findViewById(R.id.usernameFriend1);
		name.setText(array.get(position).getUser().getUsername());

		ImageView avatar = (ImageView) row.findViewById(R.id.avatarFriend1);

		ParseFile pf = (ParseFile) array.get(position).getUser().get("avatar");
		if (pf != null) {
			pf.getDataInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] data, ParseException e) {
					if (e == null) {
						setData(data, row);
					} else {
						e.printStackTrace();
					}
				}
			});
		} else {
			avatar.setImageResource(R.drawable.default_avatar);
		}

		ImageView add = (ImageView) row.findViewById(R.id.buttonFriend2);
		add.setImageResource(R.drawable.bu_add);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Handle add friend
				addPendingListener(hp.getUser(), index);
			}
		});
		return (row);
	}

	private void addPendingListener(ParseUser u, int row) {
		try {
			final ParseUser pUser = u;
			final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
			final int i = row;
			alert.setMessage(
					"Are you sure you want to add " + pUser.getUsername()
							+ " ?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									addPending(pUser);
									addPendingSqlite(pUser);
									array.remove(i);
									notifyDataSetChanged();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addPending(ParseUser u) {
		ParseUser currUser = ParseUser.getCurrentUser();
		try {
			ParseObject pendingObj = new ParseObject("Pending");
			pendingObj.put("user2", currUser);
			pendingObj.put("user1", u);
			pendingObj.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException arg0) {
					Toast.makeText(mContext, "Adding Success",
							Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	private void addPendingSqlite(ParseUser pu) {	
		SQLiteDatabase myDb = SQLiteDatabase.openOrCreateDatabase(
				"/data/data/com.nish/databases/nish_user.db", null);
		ContentValues newValues = new ContentValues();
		newValues.put("pendingId", pu.getObjectId());
		myDb.insert("pending", null, newValues);
		myDb.close();
	}

	private void setData(byte[] data, View row) {
		ImageView avatar = (ImageView) row.findViewById(R.id.avatarFriend1);
		if (data == null || data.length == 0) {
			avatar.setImageResource(R.drawable.default_avatar);
		} else {
			Bitmap bitm = BitmapFactory.decodeByteArray(data, 0, data.length);
			avatar.setImageBitmap(bitm);
		}
	}

}