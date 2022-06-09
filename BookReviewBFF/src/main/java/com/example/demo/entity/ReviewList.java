package com.example.demo.entity;

public class ReviewList {
	Iterable<Review> reviewListPage;
	
	public ReviewList() {
	}
	
	public ReviewList(Iterable<Review> reviewListPage) {
		this.reviewListPage = reviewListPage;
	}
	
	public void Review(Iterable<Review> reviewListPage) {
		this.reviewListPage = reviewListPage;
	}
	
	public Iterable<Review> getReviewListPage() {
		return reviewListPage;
	}
}
