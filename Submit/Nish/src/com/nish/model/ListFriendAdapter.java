package com.nish.model;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nish.FriendActivity;
import com.nish.R;
import com.parse.DeleteCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ListFriendAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<FriendRow> array;
	private FriendActivity fa;

	public ArrayList<FriendRow> getArray() {
		return array;
	}

	public void setArray(ArrayList<FriendRow> array) {
		this.array = array;
	}

	public FriendActivity getFa() {
		return fa;
	}

	public void setFa(FriendActivity fa) {
		this.fa = fa;
	}

	public ListFriendAdapter(Context c) {
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
		ImageView remove = (ImageView) row.findViewById(R.id.buttonFriend2);
		remove.setImageResource(R.drawable.bu_remove);
		remove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Handle add friend
				removeFriend(hp.getPending(), hp.getUser(), index);
			}
		});

		ImageView phone = (ImageView) row.findViewById(R.id.buttonFriend1);
		phone.setImageResource(R.drawable.bu_phone);
		phone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				phoneFriend(hp.getUser());
			}
		});
		return (row);
	}

	private void phoneFriend(ParseUser u) {
		try {
		final ParseUser pUser = u;
		final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		final String phone = "tel:" + pUser.getString("phone");
		alert.setMessage(
				"Are you sure you want to call " + pUser.getString("phone")
						+ " ?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									Intent callIntent = new Intent(
											Intent.ACTION_CALL);
									callIntent.setData(Uri.parse(phone));
									fa.startActivity(callIntent);
								} catch (ActivityNotFoundException e) {
									Log.e("helloandroid dialing example",
											"Call failed", e);
								}
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

	private void removeFriend(ParseObject po, ParseUser u, int row) {
		try {
		final ParseObject pObj = po;
		final ParseUser pu = u;
		final int i = row;
		final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		alert.setMessage(
				"Are you sure you want to remove " + u.getUsername() + " ?")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								removeFYes(pObj);
								removeFriendSqlite(pu);
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

	private void removeFYes(ParseObject po) {
		try {
			ParseObject friendRow = po;
			friendRow.deleteInBackground(new DeleteCallback() {

				@Override
				public void done(ParseException e) {
					if (e == null) {
						Toast.makeText(mContext, "Delete success",
								Toast.LENGTH_SHORT).show();
					} else {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void removeFriendSqlite(ParseUser pu) {
		SQLiteDatabase myDb = SQLiteDatabase.openOrCreateDatabase(
				"/data/data/com.nish/databases/nish_user.db", null);
		myDb.delete("friend", "friendId='" + pu.getObjectId() + "'", null);
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