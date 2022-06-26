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
	
	// ReviewのIDを指定してRVを1件取得
	@Override
	public Optional<Review> selectOneById(int id){
		logger.log(Level.FINER, "selectOneById(" + id + ")");
		logger.log(Level.FINER, "reviewRepository.findById(" + id + ")");
		return reviewRepository.findById(id);
	}
	
	// Reviewを指定してReviewを1件削除
	@Override
	public void deleteOneById(int id) {
		logger.log(Level.FINER, "deleteOneById(" + id + ")");
		logger.log(Level.FINER, "reviewRepository.deleteById(" + id + ")");
		reviewRepository.deleteById(id);
	}
	
	// Reviewを1件登録
	@Override
	public void insertOne(Review review) {
		logger.log(Level.FINER, "insertOne(" + review + ")");
		logger.log(Level.FINER, "reviewRepository.save(" + review + ")");
		reviewRepository.save(review);
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
	public Iterable<TotalEvaluation> selectTotalEvaluationByBookId(List<Integer> bookids){
		logger.log(Level.FINER, "selectTotalEvaluationByBookId(" + bookids + ")");
		logger.log(Level.FINER, "reviewRepository.findTotalEvaluationByBookId(" + bookids + ")");
		return reviewRepository.findTotalEvaluationByBookId(bookids);
	}

	// 上位n件のTotalEvaluationを取得
	@Override
	public Iterable<TotalEvaluation> selectTotalEvaluationTopN(int n) {
		logger.log(Level.FINER, "selectTopN(" + n + ")");
		logger.log(Level.FINER, "bookRepository.selectTotalEvaluationTopN(" + n + ")");
		return reviewRepository.selectTotalEvaluationTopN(n);
	}
	 
	// TotalEvaluationをlimit単位で分割取得する際のページ数を取得
	
	
	// 登録されている全Bookをlimit単位でページ分割し指定したpageに含まれるBook一覧を取得
//	@Override
//	public Iterable<Book> selectAllDescByPage(int page, int limit) {
//		logger.log(Level.FINER, "selectAllDescByPage(" + page + ", " + limit + ")");
//		logger.log(Level.FINER, "bookRepository.selectAllDescByLimitOffset(" + limit + ", " + limit*(page-1) + ")");
//		return bookRepository.selectAllDescByLimitOffset(limit, limit*(page-1));
//	}
	 
	// 登録されている全ReviewからTotalEvaluationを算出したものをlimit単位でページ分割し、指定したpageに含まれるTotalEvaluation一覧を取得	
	@Override
	public Iterable<TotalEvaluation> selectTotalEvaluationDescByLimitOffset(int page, int limit){
		return reviewRepository.selectTotalEvaluationDescByLimitOffset(limit, limit*(page-1));
	}
}
