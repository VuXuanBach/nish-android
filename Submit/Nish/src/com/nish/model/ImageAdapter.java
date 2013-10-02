package com.nish.model;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<byte[]> array;

	public ArrayList<byte[]> getArray() {
		return array;
	}

	public void setArray(ArrayList<byte[]> array) {
		this.array = array;
	}

	public ImageAdapter(Context c) {
		mContext = c;
		array = new ArrayList<byte[]>();
	}

	@Override
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
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some
			// attributes
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(120, 120));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(30, 10, 30, 10);
		} else {
			imageView = (ImageView) convertView;
		}

		byte[] blob = array.get(position);
		Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);		
		imageView.setImageBitmap(bmp);
		notifyDataSetChanged();

		return imageView;
	}

}