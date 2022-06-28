package com.example.demo.entity;

public class Book {
	private int id; // bookid
	private String title; // Bookのタイトル
	private String overview; // Bookの概要
	private double totalevaluation; // Bookの総合評価
	
	public Book() {
	}
	
	public Book(int id, String title, String overview, double totalevaluation) {
		this.id = id;
		this.title = title;
		this.overview = overview;
		this.totalevaluation = totalevaluation;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
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
