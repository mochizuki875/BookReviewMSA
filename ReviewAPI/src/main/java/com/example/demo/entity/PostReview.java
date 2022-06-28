package com.example.demo.entity;

public class PostReview {
	String user; // ユーザー
	int evaluation = 0; // Bookの評価
	String content; // Bookのレビュー内容
	int bookid; // 対象のbookid
	int userid = 0; // ユーザーID
	
	public PostReview() {
	}
	
	public PostReview(String user, int evaluation, String content, int bookid, int userid) {
		this.user = user;
		this.evaluation = evaluation;
		this.content = content;
		this.bookid = bookid;
		this.userid = userid;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public int getEvaluation() {
		return evaluation;
	}
	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getBookid() {
		return bookid;
	}
	public void setBookid(int bookid) {
		this.bookid = bookid;
	}	

	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}		
}
