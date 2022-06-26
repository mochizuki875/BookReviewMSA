package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.Review;
import com.example.demo.entity.TotalEvaluation;

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
	
	// 指定したbookidのTotalEvaluationを取得
	Iterable<TotalEvaluation> selectTotalEvaluationByBookId(List<Integer> bookids);
	 
	// 上位n件のTotalEvaluationを取得
	Iterable<TotalEvaluation> selectTotalEvaluationTopN(int n);
	 
	// TotalEvaluationをlimit単位で分割取得する際のページ数を取得
	
	 
	// 登録されている全ReviewからTotalEvaluationを算出したものをlimit単位でページ分割し、指定したpageに含まれるTotalEvaluation一覧を取得	
	Iterable<TotalEvaluation> selectTotalEvaluationDescByLimitOffset(int page, int limit); 
	
}
