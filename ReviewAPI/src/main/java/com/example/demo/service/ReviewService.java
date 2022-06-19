package com.example.demo.service;

import java.util.Optional;

import com.example.demo.entity.Review;

public interface ReviewService {

	// ReviewのIDを指定してRVを1件取得
	Optional<Review> selectOneById(int id);
	
	// Reviewを指定してReviewを1件削除
	void deleteOneById(int id);

	// Reviewを1件登録
	void insertOne(Review review);
	
	// bookidを指定してReviewを全件取得
	 Iterable<Review> selectAllByBookId(int bookid);
			
	// bookidを指定してReviewを全件削除（bookidに紐付くもの全て）
	 void deleteAllByBookId(int bookid);
}
