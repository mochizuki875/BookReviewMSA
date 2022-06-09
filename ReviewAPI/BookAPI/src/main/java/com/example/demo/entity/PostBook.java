package com.example.demo.entity;

public class PostBook {
	String user; // ユーザー
	String title; // 本のタイトル
	String overview; // 本の概要
	
	public PostBook() {
	}
	
	public PostBook(String user, String title, String overview) {
		this.user = user;
		this.title = title;
		this.overview = overview;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getOverview() {
		return overview;
	}
	
	public void setOverview(String overview) {
		this.overview = overview;
	}
}
