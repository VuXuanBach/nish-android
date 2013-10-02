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
import com.parse.DeleteCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ListPendingAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<FriendRow> array;
	
	public ArrayList<FriendRow> getArray() {
		return array;
	}

	public void setArray(ArrayList<FriendRow> array) {
		this.array = array;
	}

	public ListPendingAdapter(Context c) {		
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
					}
				}
			});
		} else {
			avatar.setImageResource(R.drawable.default_avatar);
		}

		ImageView add = (ImageView) row.findViewById(R.id.buttonFriend1);
		add.setImageResource(R.drawable.bu_ok);
		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Handle accept friend
				acceptFriend(hp.getPending(), hp.getUser(), index);
			}
		});

		ImageView decline = (ImageView) row.findViewById(R.id.buttonFriend2);
		decline.setImageResource(R.drawable.bu_decline);
		decline.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Handle decline friend
				declineFriend(hp.getPending(), hp.getUser(), index);
			}
		});
		return (row);
	}

	private void acceptFriend(ParseObject po, ParseUser pu, int row) {
		try {
		final int i = row;
		final ParseObject pending = po;
		final ParseUser u = pu;
		final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		alert.setMessage(
				"Are you sure you want to add " + pu.getUsername() + " ?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								deleteObjPending(pending, true);
								addFriendSqlite(u);
								removePendingSqlite(u);
								array.remove(i);
								notifyDataSetChanged();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void declineFriend(ParseObject po, ParseUser pu, int row) {
		try {
		final int i = row;
		final ParseObject pending = po;
		final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		alert.setMessage(
				"Are you sure you want to decline " + pu.getUsername() + " ?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								deleteObjPending(pending, false);
								array.remove(i);
								notifyDataSetChanged();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteObjPending(ParseObject po, boolean accept) {
		final String message = accept ? "Accept success" : "Decline success";
		try {
			ParseObject pending = po;
			if (accept) {
				ParseUser u1 = pending.getParseUser("user1");
				ParseUser u2 = pending.getParseUser("user2");
				ParseObject friendObj = new ParseObject("Friend");
				friendObj.put("user1", u1);
				friendObj.put("user2", u2);
				friendObj.saveEventually();
			}

			pending.deleteInBackground(new DeleteCallback() {

				@Override
				public void done(ParseException e) {
					if (e == null) {
						Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
								.show();
					} else {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void addFriendSqlite(ParseUser pu) {
		SQLiteDatabase myDb = SQLiteDatabase.openOrCreateDatabase(
				"/data/data/com.nish/databases/nish_user.db", null);
		ContentValues newValues = new ContentValues();
		newValues.put("friendId", pu.getObjectId());
		myDb.insert("friend", null, newValues);
		myDb.close();
	}
	
	private void removePendingSqlite(ParseUser pu) {
		SQLiteDatabase myDb = SQLiteDatabase.openOrCreateDatabase(
				"/data/data/com.nish/databases/nish_user.db", null);
		myDb.delete("pending", "pendingId='" + pu.getObjectId() + "'", null);
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