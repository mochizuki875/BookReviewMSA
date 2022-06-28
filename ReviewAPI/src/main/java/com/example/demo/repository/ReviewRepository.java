package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Review;
import com.example.demo.entity.TotalEvaluation;

//CrudRepositoryの拡張インターフェースとしてRepositoryを作成
public interface ReviewRepository extends CrudRepository<Review, Integer> {
	// 独自で使用したいメソッドを定義
	// bookidを指定してReviewを全件取得
	@Query("SELECT * FROM review WHERE bookid= :bookid;")
	Iterable<Review> findAllByBookid(@Param("bookid") int bookid);
	
	// bookidを指定してReviewを全件削除（DML系クエリを実行する際は@Modifyingが必要）
	@Modifying
	@Query("DELETE FROM review WHERE bookid= :bookid;")
	void deleteAllByBookid(@Param("bookid") int bookid);
	
	// bookidを指定してTotalEvaluationを取得
	@Query("SELECT ROUND(AVG(evaluation),1) AS totalevaluation FROM review WHERE bookid = :bookid GROUP BY bookid;")
	double findTotalEvaluationByBookId(@Param("bookid") int bookid);
	
	// bookidを複数指定してTotalEvaluationを取得
	@Query("SELECT * FROM (SELECT bookid, ROUND(AVG(evaluation),1) AS value FROM review GROUP BY bookid) totalEvaluations WHERE bookid IN (:bookids);")
	Iterable<TotalEvaluation> findTotalEvaluationByBookIds(@Param("bookids") List<Integer> bookids);
	
	// 上位n件のTotalEvaluationを取得
	@Query("SELECT bookid, ROUND(AVG(evaluation),1) AS value FROM review GROUP BY bookid ORDER BY value DESC, bookid ASC LIMIT :n;")
	Iterable<TotalEvaluation> selectTotalEvaluationTopN(@Param("n") int n);
	
	// valueが高い順にTotalEvaluationをoffset付きでlimit件取得
	@Query("SELECT bookid, ROUND(AVG(evaluation),1) AS value  FROM review  GROUP BY bookid ORDER BY value DESC, bookid ASC LIMIT :limit OFFSET :offset;")
	Iterable<TotalEvaluation> selectTotalEvaluationDescByLimitOffset(@Param("limit") int limit, @Param("offset") int offset);
	
}
