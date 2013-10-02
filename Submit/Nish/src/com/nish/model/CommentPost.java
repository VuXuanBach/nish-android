package com.nish.model;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class CommentPost {

	private ParseObject image;
	private ParseUser user;
	private String content;

	public CommentPost() {
	}

	public CommentPost(ParseObject image, ParseUser user, String content) {
		this.image = image;
		this.user = user;
		this.content = content;
	}

	public ParseUser getUser() {
		return user;
	}

	public void setUser(ParseUser user) {
		this.user = user;
	}

	public ParseObject getImage() {
		return image;
	}

	public void setImage(ParseObject image) {
		this.image = image;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
