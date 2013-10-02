package com.nish.model;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class FriendRow {

	private ParseObject pending;
	private ParseUser user;

	public FriendRow() {
	}

	public FriendRow(ParseUser user) {
		this.user = user;
	}
	
	public FriendRow(ParseUser user, ParseObject pending) {
		this.user = user;
		this.pending = pending;
	}

	public ParseUser getUser() {
		return user;
	}

	public void setUser(ParseUser user) {
		this.user = user;
	}
	
	public ParseObject getPending() {
		return pending;
	}

	public void setPending(ParseObject pending) {
		this.pending = pending;
	}
}

