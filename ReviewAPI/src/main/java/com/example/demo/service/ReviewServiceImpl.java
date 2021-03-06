package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Review;
import com.example.demo.entity.TotalEvaluation;
import com.example.demo.repository.ReviewRepository;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
	// ReviewRepositoryインスタンス作成
	@Autowired
	ReviewRepository reviewRepository;

	Logger logger = Logger.getLogger(ReviewServiceImpl.class.getName());
	ConsoleHandler handler = new ConsoleHandler();
	
	// reviewidを指定してReviewを1件取得
	@Override
	public Optional<Review> selectOneById(int id){
		logger.log(Level.FINER, "selectOneById(" + id + ")");
		logger.log(Level.FINER, "reviewRepository.findById(" + id + ")");
		return reviewRepository.findById(id);
	}
	
	// reviewidを指定してReviewを1件削除
	@Override
	public void deleteOneById(int id) {
		logger.log(Level.FINER, "deleteOneById(" + id + ")");
		logger.log(Level.FINER, "reviewRepository.deleteById(" + id + ")");
		reviewRepository.deleteById(id);
	}
	
	// Reviewを1件登録
	@Override
	public Review insertOne(Review review) {
		logger.log(Level.FINER, "insertOne(" + review + ")");
		logger.log(Level.FINER, "reviewRepository.save(" + review + ")");
		return reviewRepository.save(review);
	}
	
	// bookidを指定してReviewを全件取得
	@Override
	public Iterable<Review> selectAllByBookId(int bookid){
		logger.log(Level.FINER, "selectAllByBookId(" + bookid + ")");
		logger.log(Level.FINER, "reviewRepository.findAllByBookid(" + bookid + ")");
	    return reviewRepository.findAllByBookid(bookid);
	}
	
	// bookidを指定してReviewを全件削除（bookidに紐付くもの全て）
	@Override
	public void deleteAllByBookId(int bookid) {
		logger.log(Level.FINER, "deleteAllByBookId(" + bookid + ")");
		logger.log(Level.FINER, "reviewRepository.deleteAllByBookid(" + bookid + ")");
		reviewRepository.deleteAllByBookid(bookid);
	}
	
	// 指定したbookidのTotalEvaluationを取得
	public double selectTotalEvaluationByBookId(int bookid) {
		logger.log(Level.FINER, "selectTotalEvaluationByBookId(" + bookid + ")");
		logger.log(Level.FINER, "reviewRepository.findTotalEvaluationByBookId(" + bookid + ")");
		return reviewRepository.findTotalEvaluationByBookId(bookid);
	}
	
	// 指定した複数bookidのTotalEvaluationを取得
	public Iterable<TotalEvaluation> selectTotalEvaluationByBookIds(List<Integer> bookids){
		logger.log(Level.FINER, "selectTotalEvaluationByBookIds(" + bookids + ")");
		logger.log(Level.FINER, "reviewRepository.findTotalEvaluationByBookIds(" + bookids + ")");
		return reviewRepository.findTotalEvaluationByBookIds(bookids);
	}

	// 上位n件のTotalEvaluationを取得
	@Override
	public Iterable<TotalEvaluation> selectTotalEvaluationTopN(int n) {
		logger.log(Level.FINER, "selectTopN(" + n + ")");
		logger.log(Level.FINER, "bookRepository.selectTotalEvaluationTopN(" + n + ")");
		return reviewRepository.selectTotalEvaluationTopN(n);
	}
	 
	// TotalEvaluationをlimit単位で分割取得する際のページ数を取得（未実装）
	 
	// 登録されている全ReviewからTotalEvaluationを算出したものをlimit単位でページ分割し、指定したpageに含まれるTotalEvaluation一覧を取得	
	@Override
	public Iterable<TotalEvaluation> selectTotalEvaluationDescByLimitOffset(int page, int limit){
		return reviewRepository.selectTotalEvaluationDescByLimitOffset(limit, limit*(page-1));
	}
}
