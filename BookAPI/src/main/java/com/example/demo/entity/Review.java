package com.example.demo.entity;

public class Review {
	private int id; // 主キー(reviewid)
	private int evaluation; // Bookの評価（1~5）
	private String content; // Bookのレビュー内容
	private int bookid; // Reviewが紐づくbookid
	private int userid; // ユーザーID
	
	public Review() {
	}
	
	public Review(int id, int evaluation, String content, int bookid, int userid) {
		this.id = id;
		this.evaluation = evaluation;
		this.content = content;
		this.bookid = bookid;
		this.userid = userid;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
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
