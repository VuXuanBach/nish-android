package com.nish.model;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nish.CommentActivity;
import com.nish.HomeActivity;
import com.nish.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class HomeAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<HomePost> array;	

	public ArrayList<HomePost> getArray() {
		return array;
	}

	public void setArray(ArrayList<HomePost> array) {
		this.array = array;
	}

	public HomeAdapter(Context c) {
		mContext = c;
		array = new ArrayList<HomePost>();
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
		final View row = inflater.inflate(R.layout.custom_home_post, null);
		
		
		final HomePost hp = array.get(position);
		// Set Photo
		byte[] blob = array.get(position).getImage();
		Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
		ImageView photo = (ImageView) row.findViewById(R.id.imagePost);
		photo.setImageBitmap(bmp);
		// Set Name
		TextView name = (TextView) row.findViewById(R.id.usernamePost);
		name.setText(array.get(position).getUser().getUsername());
		
		TextView date = (TextView) row.findViewById(R.id.datePost);
		date.setText(array.get(position).getDate());
		
		TextView location = (TextView) row.findViewById(R.id.locationTextPost);
		location.setText(array.get(position).getLocation());
		
		ImageView locationImage = (ImageView) row.findViewById(R.id.locationImagePost);
		locationImage.setImageResource(R.drawable.bu_location);
		
		ParseFile pf = (ParseFile) array.get(position).getUser().get("avatar");
		if (pf != null) {
			pf.getDataInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] data, ParseException e) {
					ImageView avatar = (ImageView) row.findViewById(R.id.avatarPost);
					if (e == null) {
						if (data != null) {
							Bitmap bitm = BitmapFactory.decodeByteArray(data, 0,
									data.length);
							avatar.setImageBitmap(bitm);
						} else {
							avatar.setImageResource(R.drawable.default_avatar);
						}
					} else {						
					}
				}
			});
		} else {
			ImageView avatar = (ImageView) row.findViewById(R.id.avatarPost);
			avatar.setImageResource(R.drawable.default_avatar);
		}
		
		Button comment = (Button) row.findViewById(R.id.commentButton);
		comment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HomeActivity.current = hp.getPo();
				Intent myIntent = new Intent(mContext, CommentActivity.class);
				mContext.startActivity(myIntent);
			}
		});

		Button like = (Button) row.findViewById(R.id.likeButton);
		TextView tv = (TextView) row.findViewById(R.id.usernamePeopleLike);

		String likeString = "";
		for (String s : hp.getLike()) {
			likeString += s + ", ";
		}
		if (!likeString.trim().equals("")) {

			tv.setText(likeString.substring(0, likeString.length() - 2));
			ImageView iv = (ImageView) row.findViewById(R.id.likeImagePost);
			iv.setImageResource(R.drawable.bu_like);
		}

		like.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean check = true;
				List<String> arrs = hp.getLike();
				if (arrs == null || arrs.size() == 0) {
					arrs = new ArrayList<String>();
				}
				for (String likeStr : arrs) {
					if (likeStr
							.equals(ParseUser.getCurrentUser().getUsername())) {
						check = false;
						break;
					}
				}
				try {
					if (check) {
						arrs.add(ParseUser.getCurrentUser().getUsername());
						hp.getPo().addUnique("like",
								ParseUser.getCurrentUser().getUsername());
					} else {
						arrs.remove(ParseUser.getCurrentUser().getUsername());
					}
					hp.getPo().saveEventually();
					hp.setLike(arrs);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					TextView tv = (TextView) row
							.findViewById(R.id.usernamePeopleLike);
					ImageView iv = (ImageView) row
							.findViewById(R.id.likeImagePost);
					String likeString = "";
					for (String s : arrs) {
						likeString += s + ", ";
					}
					Log.d("CHECK", likeString + "");
					if (arrs.size() > 0) {
						tv.setText(likeString.substring(0, likeString.length()-2));
						iv.setImageResource(R.drawable.bu_like);
						iv.setVisibility(ImageView.VISIBLE);
					} else {
						tv.setText("");
						iv.setVisibility(ImageView.INVISIBLE);
					}
				}
			}
		});
		return (row);
	}	
}