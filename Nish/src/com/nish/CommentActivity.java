package com.nish;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.nish.model.CommentPost;
import com.nish.model.Utility;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class CommentActivity extends SherlockActivity {
	private EditText comment;
	private int sh, left, bottom;
	private ScrollView scrlv;
	private static ArrayList<CommentPost> comments;
	private static byte[] photo, avatarPhoto;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		try {
			Parse.initialize(this, "PhPbACGcB2vstnumIcUX1D1WrOzabqFPognqfufu",
					"K5fWoItwvDbFXXPyYL11J3t4thAW3YQw3oUyeS7P");
		} catch (Exception e) {
		}
		try {
			setRequestedOrientation(Utility.NOSENSOR);
			setContentView(R.layout.activity_comment);
			setTheme(Utility.THEME);
			this.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			Utility.displayErrorConnection(this);
			retrieveTop();
			retrieveComment();

			Button submit = (Button) findViewById(R.id.submitComment);
			submit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					submitComment();
				}
			});

			scrlv = (ScrollView) findViewById(R.id.scrollComment);

			DisplayMetrics dms = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dms);
			sh = dms.heightPixels;

			comment = (EditText) findViewById(R.id.commentText);
			comment.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (comment.hasFocus()) {
						left = comment.getLeft();
						bottom = comment.getTop();
						if (bottom > sh / 3) {
							scrlv.scrollTo(left, (sh / 3));
							left = comment.getLeft();
							bottom = comment.getTop();
						}
					}
				}
			});

			comment.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEND) {
						submitComment();
						return true;
					}
					return false;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retrieveTop() {
		comments = new ArrayList<CommentPost>();

		ParseFile pf = (ParseFile) HomeActivity.current.get("imageFile");
		pf.getDataInBackground(new GetDataCallback() {

			@Override
			public void done(byte[] bytes, ParseException e) {
				if (e == null) {
					CommentActivity.photo = bytes;
				} else {
					e.printStackTrace();
				}
			}
		});
		Bitmap photoPic = BitmapFactory.decodeByteArray(photo, 0, photo.length);

		ParseUser u = HomeActivity.current.getParseUser("user");
		TextView text = ((TextView) findViewById(R.id.usernamePostComment));
		text.setText(u.getString("username"));

		((ImageView) findViewById(R.id.imageComment)).setImageBitmap(photoPic);

		ParseFile pfa = (ParseFile) u.get("avatar");
		if (pfa != null) {
			pfa.getDataInBackground(new GetDataCallback() {

				@Override
				public void done(byte[] bytes, ParseException e) {
					if (e == null) {
						CommentActivity.avatarPhoto = bytes;
					} else {
						e.printStackTrace();
					}
				}
			});
		} else {
			avatarPhoto = null;
		}
		if (avatarPhoto == null || avatarPhoto.length == 0) {
			((ImageView) findViewById(R.id.avatarPostComment))
					.setImageResource(R.drawable.default_avatar);
		} else {
			Bitmap avatarPic = BitmapFactory.decodeByteArray(avatarPhoto, 0,
					avatarPhoto.length);
			((ImageView) findViewById(R.id.avatarPostComment))
					.setImageBitmap(avatarPic);
		}
	}

	private void retrieveComment() {
		ParseQuery query = new ParseQuery("Comment");
		query.include("user");
		query.include("image");
		query.whereEqualTo("image", HomeActivity.current);
		query.orderByAscending("createdAt");

		query.findInBackground(new FindCallback() {

			@Override
			public void done(List<ParseObject> comments, ParseException e) {
				if (e == null) {
					for (int j = 0; j < comments.size(); j++) {
						CommentPost c = new CommentPost();
						CommentActivity.comments.add(c);
					}
					int i = 0;
					for (ParseObject po : comments) {
						CommentPost cp = CommentActivity.comments.get(i);
						cp.setUser(po.getParseUser("user"));
						cp.setImage(po.getParseObject("image"));
						cp.setContent(po.getString("comment"));

						CommentActivity.comments.set(i, cp);
						i++;
					}
					displayComment();
				} else {
					e.printStackTrace();
				}
			}
		});
	}

	private void submitComment() {
		EditText et = ((EditText) findViewById(R.id.commentText));
		String newComment = et.getText().toString();
		if (ParseUser.getCurrentUser() != null) {
			if (!newComment.trim().equals("")) {
				CommentPost co = new CommentPost(HomeActivity.current,
						ParseUser.getCurrentUser(), newComment);
				comments.add(co);
			}
			ParseObject po = new ParseObject("Comment");
			po.put("user", ParseUser.getCurrentUser());
			po.put("image", HomeActivity.current);
			po.put("comment", newComment);
			po.saveInBackground();
		}
		displayComment();
		comment.setText("");

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(comment.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void displayComment() {
		try {
			LinearLayout layout = (LinearLayout) findViewById(com.nish.R.id.layoutOldComment);
			layout.removeAllViews();
			for (int y = 0; y < comments.size(); y++) {

				LayoutInflater inflater = (LayoutInflater) CommentActivity.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater
						.inflate(R.layout.custom_comment_post, null);
				CommentPost cp = comments.get(y);

				// Set Name
				TextView name = (TextView) view
						.findViewById(R.id.usernameComment1);
				name.setText(cp.getUser().getUsername());

				// Set avatar
				final ImageView avatarView = (ImageView) view
						.findViewById(R.id.avatarComment);

				ParseFile pfa = (ParseFile) cp.getUser().get("avatar");
				if (pfa != null) {
					pfa.getDataInBackground(new GetDataCallback() {

						@Override
						public void done(byte[] bytes, ParseException e) {
							if (e == null) {
								displayAvatar(avatarView, bytes);
							} else {
								e.printStackTrace();
							}
						}
					});
				} else {
					displayAvatar(avatarView, null);
				}

				// Set content
				TextView comment = (TextView) view
						.findViewById(R.id.contentComment);
				comment.setText(cp.getContent());

				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layout.addView(view, layoutParams);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void displayAvatar(ImageView view, byte[] bytes) {
		if (bytes == null) {
			view.setImageResource(R.drawable.default_avatar);
		} else {
			Bitmap bitm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			view.setImageBitmap(bitm);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("Back").setIcon(R.drawable.ic_prev)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		menu.add("Refresh").setIcon(R.drawable.ic_refresh)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().toString().equalsIgnoreCase("back")) {
			finish();
		}
		if (item.getTitle().toString().equalsIgnoreCase("Refresh")) {
			Utility.displayErrorConnection(this);
			try {
				retrieveComment();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.gc();
	}
}