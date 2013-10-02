package com.nish.model;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class HomePost {
	private ParseUser user;
	private List<String> like;
	private byte[] image;
	private String date;
	private String location;
	private ParseObject po;

	public HomePost() {
		this.user = new ParseUser();
		this.image = new byte[0];
		this.date = "";
		this.location = "";
		this.like = new ArrayList<String>();
		this.po = null;
	}

	public HomePost(ParseUser user, byte[] image, String date, ParseObject po,
			List<String> like, String location) {
		this.user = user;
		this.image = image;
		this.date = date;
		this.po = po;
		this.like = like;
		this.location = location;
	}

	public List<String> getLike() {
		return like;
	}

	public void setLike(List<String> like) {
		this.like = like;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public ParseUser getUser() {
		return user;
	}

	public void setUser(ParseUser user) {
		this.user = user;
	}

	public ParseObject getPo() {
		return po;
	}

	public void setPo(ParseObject po) {
		this.po = po;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
