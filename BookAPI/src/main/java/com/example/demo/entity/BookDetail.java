package com.example.demo.entity;

public class BookDetail {

	Book book;
	Iterable<Review> reviewList;
	
	public BookDetail() {
	}
	
	public BookDetail(Book book, Iterable<Review> reviewList) {
		this.book = book;
		this.reviewList = reviewList;
	}
	
	public void setBook(Book book) {
		this.book = book;
	}
	
	public Book getBook() {
		return book;
	}
	
	public void setReviewList(Iterable<Review> reviewList) {
		this.reviewList = reviewList;
	}
	
	public Iterable<Review> getReviewList() {
		return reviewList;
	}
	
	
}
