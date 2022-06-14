package com.example.demo.entity;

public class PostBook {
	String user; // ユーザー
	String title; // 本のタイトル
	String overview; // 本の概要
	double totalevaluation; // 本の総合評価
	
	public PostBook() {
	}
	
	public PostBook(String user, String title, String overview, double totalevaluation) {
		this.user = user;
		this.title = title;
		this.overview = overview;
		this.totalevaluation = totalevaluation;
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
	
	public double getTotalevaluation() {
		return totalevaluation;
	}
	
	public void setTotalevaluation(double totalevaluation) {
		this.totalevaluation = totalevaluation;
	}
}
