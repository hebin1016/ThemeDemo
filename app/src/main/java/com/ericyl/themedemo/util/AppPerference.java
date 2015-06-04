package com.ericyl.themedemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPerference {
	private String username;
	private boolean isRememberUsername;

	private static final String APP_PERFERENCE = "Perference";
	private static final String USER_NAME = "Username";
	private static final String IS_REMEMBER_USERNAME = "Is_Remember_Username";

	public AppPerference() {
	}

	public AppPerference(String username, boolean isRememberUsername) {
		super();
		this.username = username;
		this.isRememberUsername = isRememberUsername;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isRememberUsername() {
		return isRememberUsername;
	}

	public void setRememberUsername(boolean isRememberUsername) {
		this.isRememberUsername = isRememberUsername;
	}

	public static AppPerference Read() {
		Context context = AppProperties.getContext();
		AppPerference appPerference = new AppPerference();
		try {
			SharedPreferences share = context.getSharedPreferences(
					APP_PERFERENCE, Context.MODE_PRIVATE);
			appPerference.setUsername(share.getString(USER_NAME, null));
			appPerference.setRememberUsername(share.getBoolean(
					IS_REMEMBER_USERNAME, false));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return appPerference;
	}

	public static void Write(AppPerference appPerference) {
		Context context = AppProperties.getContext();
		try {
			SharedPreferences share = context.getSharedPreferences(
					APP_PERFERENCE, Context.MODE_PRIVATE);
			if (share != null) {
				Editor editor = share.edit();
				String username = appPerference.getUsername();
				if (username != null && !username.equals("")) {
					editor.putString(USER_NAME, username);
				}
				boolean isRememberUsername = appPerference.isRememberUsername();
				editor.putBoolean(IS_REMEMBER_USERNAME, isRememberUsername);
				editor.commit();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
