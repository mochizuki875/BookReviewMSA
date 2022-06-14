package com.example.demo.controller;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.demo.entity.Book;
import com.example.demo.entity.PostBook;
import com.example.demo.entity.PostReview;
import com.example.demo.entity.Review;
import com.example.demo.entity.ReviewList;
import com.example.demo.service.ReviewService;


//REST APIを示すアノテーション（戻り値がViewではなくJson/XML）
@RestController
public class ReviewAPIController {
	
	final String BOOK_API_URL = "http://127.0.0.1:8082/api/book"; // Book APIのURL
	
	final int TOP_NUMBER = 10; // トップページの表示件数
	final int PAGE_SIZE = 10; // 1ページあたりの表示件数
	
	// Serviceインスタンスを作成
	@Autowired
	ReviewService reviewService;
	
	// RestTemplateインスタンス作成
	RestTemplate restTemplate = new RestTemplate();
	
	Logger logger = Logger.getLogger(ReviewAPIController.class.getName());
	ConsoleHandler handler = new ConsoleHandler();

	// RV一覧取得API
	@GetMapping("/api/review")
	public ReviewList getReview(@RequestParam(value="user", required=false) String user, @RequestParam(value="bookid", required=true, defaultValue = "0") int bookid) {
		try {
			logger.log(Level.INFO, "GET /api/review/?user=" + user + "&bookid=" + bookid);
			
			logger.log(Level.INFO, "Get book review.(bookid=" + bookid + ")");
			logger.log(Level.FINE, "bookService.selectOneById(" + bookid + ")");
			
			ReviewList reviewList = new ReviewList(); // ReviewListインスタンスを作成
			reviewList.setReviewListPage(reviewService.selectAllByBookId(bookid)); // RV一覧を取得
			
			return reviewList; 
		}
		catch (HttpClientErrorException e) {
			throw e;
		}
		catch (HttpServerErrorException e) {
			throw e;
		} 
	}
	
	// RV新規登録API
	@PostMapping("/api/review/insert")
	public Review insertReview(@RequestBody PostReview postReview) {
		try {
			logger.log(Level.INFO, "POST /review/insert");
			logger.log(Level.INFO, "user: " + postReview.getUser());
			logger.log(Level.INFO, "evaluation: " + postReview.getEvaluation());
			logger.log(Level.INFO, "content: " + postReview.getContent());
			logger.log(Level.INFO, "bookid: " + postReview.getBookid());
			logger.log(Level.INFO, "userid: " + postReview.getUserid());
			
			String user = postReview.getUser();
			int bookid = postReview.getBookid();
			
			logger.log(Level.INFO, "Create Review instance.");
			Review review = new Review();
			review.setEvaluation(postReview.getEvaluation());
			review.setContent(postReview.getContent());
			review.setBookid(postReview.getBookid());
			review.setUserid(postReview.getUserid());
			
			logger.log(Level.INFO, "Insert review.");
			logger.log(Level.FINE, "reviewService.insertOne(" + review + ")");
			reviewService.insertOne(review); // Reviewを登録
			
			logger.log(Level.INFO, "Get reviews of bookid = " + review.getBookid() + ".");
			logger.log(Level.FINE, "reviewService.selectAllByBookId(" + review.getBookid() + ")");
			Iterable<Review> reviewList = reviewService.selectAllByBookId(review.getBookid()); // 対象BookのReviewを全て取得
			
			// 取得したReviewのevaluationの平均値を算出
			double totalevaluation = 0.0;
			int counter = 0;
			for(Review tempReview : reviewList) {
				totalevaluation += tempReview.getEvaluation();
				counter ++;
			}
			
			// Book更新API実行
			logger.log(Level.INFO, "Create PostBook instance.");
			PostBook postBook = new PostBook();
			postBook.setTotalevaluation((double)Math.round(totalevaluation*10/counter)/10);
			
			HttpEntity<PostBook> entity = new HttpEntity<>(postBook, null);
			
			logger.log(Level.INFO, "Insert book.");
			logger.log(Level.INFO, "Set totalEvaluation.");
			
			logger.log(Level.INFO, "POST " + BOOK_API_URL + "/" + bookid + "/update?user=" + user);
			ResponseEntity<Book> responseBook = restTemplate.exchange(BOOK_API_URL + "/{bookid}/update?user={user}", HttpMethod.POST, entity, Book.class, bookid, user);
			
			return review;
		}
		catch (HttpClientErrorException e) {
			throw e;
		}
		catch (HttpServerErrorException e) {
			throw e;
		} 
	}
	
	// RV削除API
	// 指定したreviewidのRVを削除
	@DeleteMapping("/api/review/{reviewid}")
	public void deleteReview(@RequestParam(value="user", required=false) String user, @PathVariable int reviewid) {
		try {
			logger.log(Level.INFO, "DELETE /api/review/" + reviewid);
			logger.log(Level.INFO, "user: " + user);
			
			
			int bookid =reviewService.selectOneById(reviewid).get().getBookid(); // 削除対象Reviewのbookidを取得
			
			
			logger.log(Level.INFO, "Delete review of reviewid=" + reviewid + ".");
			logger.log(Level.FINE, "reviewService.deleteOneById(" + reviewid + ")");
			reviewService.deleteOneById(reviewid); // idを指定してReviewを削除
			
			// totalevaluation再計算
			logger.log(Level.FINE, "reviewService.selectAllByBookId(" + bookid + ")");
			Iterable<Review> reviewList = reviewService.selectAllByBookId(bookid); // 対象BookのReviewを全て取得
			
			// 取得したReviewのevaluationの平均値を算出
			double totalevaluation = 0.0;
			int counter = 0;
			for(Review tempReview : reviewList) {
				totalevaluation += tempReview.getEvaluation();
				counter ++;
			}
			
			// Book更新API実行
			logger.log(Level.INFO, "Create PostBook instance.");
			PostBook postBook = new PostBook();
			postBook.setTotalevaluation((double)Math.round(totalevaluation*10/counter)/10);
			
			HttpEntity<PostBook> entity = new HttpEntity<>(postBook, null);
			
			logger.log(Level.INFO, "Insert book.");
			logger.log(Level.INFO, "Set totalEvaluation.");
			
			logger.log(Level.INFO, "POST " + BOOK_API_URL + "/" + bookid + "/update?user=" + user);
			ResponseEntity<Book> responseBook = restTemplate.exchange(BOOK_API_URL + "/{bookid}/update?user={user}", HttpMethod.POST, entity, Book.class, bookid, user);
			
		}
		catch (HttpClientErrorException e) {
			throw e;
		}
		catch (HttpServerErrorException e) {
			throw e;
		} 
	}
	
	// RV全件削除API
	// 指定したbookidに紐付くRVを全件削除
	@DeleteMapping("/api/review/all")
	public void deleteReviewAll(@RequestParam(value="user", required=false) String user, @RequestParam(value="bookid", required=true) int bookid) {
		try {
			logger.log(Level.INFO, "DELETE /api/review/all?bookid=" + bookid);
			logger.log(Level.INFO, "user: " + user);
			
			logger.log(Level.INFO, "Delete all reviews related to bookid = " + bookid + ".");
			logger.log(Level.FINE, "reviewService.deleteAllByBookId(" + bookid + ")");
			reviewService.deleteAllByBookId(bookid); // Bookに紐付くReviewを全件削除
		}
		catch (HttpClientErrorException e) {
			throw e;
		}
		catch (HttpServerErrorException e) {
			throw e;
		} 
	}
}
