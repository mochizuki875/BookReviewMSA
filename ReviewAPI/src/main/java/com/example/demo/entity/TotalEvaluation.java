package com.example.demo.entity;

public class TotalEvaluation {
	private int bookid;
	private double value = 0; // totalevaluation（1~5）
	
	public TotalEvaluation() {
	}
	
	public TotalEvaluation(int bookid, double value) {
		this.bookid = bookid;
		this.value = value;
	}
	
	public int getBookid() {
		return bookid;
	}
	
	public void setBookid(int bookid) {
		this.bookid = bookid;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
}
