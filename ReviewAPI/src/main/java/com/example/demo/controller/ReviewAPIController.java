package com.example.demo.controller;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entity.Book;
import com.example.demo.entity.PostBook;
import com.example.demo.entity.PostReview;
import com.example.demo.entity.Review;
import com.example.demo.entity.ReviewList;
import com.example.demo.entity.TotalEvaluation;
import com.example.demo.service.ReviewService;


//REST APIを示すアノテーション（戻り値がViewではなくJson/XML）
@RestController
public class ReviewAPIController {
	
	final int TOP_NUMBER = 10; // トップページの表示件数
	final int PAGE_SIZE = 10; // 1ページあたりの表示件数
	
	// final String BOOK_API_URL = "http://127.0.0.1:8082/api/book"; // Book APIのURL
	final String BOOK_API_URL = System.getenv("BOOK_API_URL");
	
	// Service作成
	@Autowired
	ReviewService reviewService;
	
	// RestTemplate作成
	RestTemplate restTemplate = new RestTemplate();
	
	// Logger作成
	Logger logger = Logger.getLogger(ReviewAPIController.class.getName());
	ConsoleHandler handler = new ConsoleHandler();

	// Review一覧取得API
	// リクエストパラメータで指定されたbookidに紐付くReviewを返す
	@GetMapping("/api/review")
	public ReviewList getReviewList(@RequestParam(value="user", required=false) String user, @RequestParam(value="bookid", required=true) int bookid) {
		try {
			logger.log(Level.INFO, "GET /api/review/?user=" + user + "&bookid=" + bookid);
			
			logger.log(Level.INFO, "Get ReviewList.(bookid=" + bookid + ")");
			ReviewList reviewList = new ReviewList(); // ReviewListインスタンスを作成
			reviewList.setReviewListPage(reviewService.selectAllByBookId(bookid)); // Review一覧を取得
			logger.log(Level.INFO, "Success to get ReviewList.(bookid=" + bookid + ")");
			
			logger.log(Level.INFO, "Return ReviewList.");
			return reviewList; 
		} 
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		}
	}
	
	// Review新規登録API
	@PostMapping("/api/review/insert")
	public Review insertReview(@RequestBody PostReview postReview) {
		try {
			logger.log(Level.INFO, "POST /review/insert");
			logger.log(Level.INFO, " user: " + postReview.getUser());
			logger.log(Level.INFO, " evaluation: " + postReview.getEvaluation());
			logger.log(Level.INFO, " content: " + postReview.getContent());
			logger.log(Level.INFO, " bookid: " + postReview.getBookid());
			logger.log(Level.INFO, " userid: " + postReview.getUserid());
			
			String user = postReview.getUser();
			int bookid = postReview.getBookid();
			
			// bookidが指定されていなければ400エラー
			if (bookid == 0) {
				logger.log(Level.SEVERE, "bookid is empty.");
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request. bookid is empty."); // restControllerのレスポンスコードとして400をthrow
			}
			
			// Book取得API実行メソッド
			// bookidに対応するBookを取得（Review登録対象のBookが存在するかを確認）
			logger.log(Level.INFO, "Get Book.(bookid=" + bookid + ")");
			ResponseEntity<Book> responseBook = getBookApi(user, bookid);
			logger.log(Level.INFO, "Success to get Book.(bookid = " + bookid + ")");
			
			// Reviewを作成
			logger.log(Level.INFO, "Create Review instance.");
			Review review = new Review();
			review.setEvaluation(postReview.getEvaluation());
			review.setContent(postReview.getContent());
			review.setBookid(postReview.getBookid());
			review.setUserid(postReview.getUserid());
			
			// Reviewを登録
			logger.log(Level.INFO, "Insert Review.");
			logger.log(Level.FINE, "reviewService.insertOne(" + review + ")");
			review = reviewService.insertOne(review);
			logger.log(Level.INFO, "Success to insert Review.(reviewid = " + review.getId() + ")");
			
			// 対象bookidのtotalevaluationを算出
			logger.log(Level.INFO, "Calculate totalevaluation of bookid = " + bookid + ".");
			double totalevaluation = reviewService.selectTotalEvaluationByBookId(bookid);
			logger.log(Level.INFO, "totalevaluation = " + totalevaluation + ".");
			logger.log(Level.INFO, "Success to calcurate totalevaluation.");
			
			// bookidに対応するBookのtotalevaluationを更新
			logger.log(Level.INFO, "Create PostBook instance.");
			PostBook postBook = new PostBook();
			postBook.setUser(user);
			postBook.setTotalevaluation(totalevaluation);
			
			// Book更新API実行メソッド
			logger.log(Level.INFO, "Update Book totalevaluation.(bookid = " + bookid + ")");
			responseBook = postBookApi(user, bookid, postBook);
			logger.log(Level.INFO, "Success to update Book.(bookid = " + bookid + ")");
			
			logger.log(Level.INFO, "Return Review.(reviewid = " + review.getId() + ")");
			return review;
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request."); // restControllerのレスポンスコードとして400をthrow
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error."); // restControllerのレスポンスコードとして500をthrow
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		}
	}
	
	// Review削除API
	// 指定したreviewidのReviewを削除
	@DeleteMapping("/api/review/{reviewid}")
	public void deleteReview(@RequestParam(value="user", required=false) String user, @PathVariable int reviewid) {
		try {
			logger.log(Level.INFO, "DELETE /api/review/" + reviewid + "?user=" + user);
			
			int bookid =reviewService.selectOneById(reviewid).get().getBookid(); // 削除対象Reviewのbookidを取得
			
			// Reviewからbookidが取得できなければ500エラー
			if (bookid == 0) {
				logger.log(Level.SEVERE, "Couldn't get bookid from Review.(reviewid = " + reviewid + ")");
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error. Couldn't get bookid from Review(reviewid = " + reviewid + ")."); // restControllerのレスポンスコードとして400をthrow
			}
			
			// Book取得API実行メソッド
			// bookidに対応するBookを取得（Review削除対象のBookが存在するかを確認）
			logger.log(Level.INFO, "Get Book.(bookid = " + bookid + ")");
			ResponseEntity<Book> responseBook = getBookApi(user, bookid);
			logger.log(Level.INFO, "Success to get Book.(bookid = " + bookid + ")");
			
			// reviewidを指定してReviewを削除
			logger.log(Level.INFO, "Delete Review.(reviewid = " + reviewid + ")");
			logger.log(Level.FINE, "reviewService.deleteOneById(" + reviewid + ")");
			reviewService.deleteOneById(reviewid); 
			logger.log(Level.INFO, "Success to delete Review.(reviewid = " + reviewid + ")");

			// 対象bookidのtotalevaluationを算出
			logger.log(Level.INFO, "Calculate totalevaluation.(bookid = " + bookid + ")");
			// double totalevaluation = calcTotalevaluation(bookid);
			double totalevaluation = reviewService.selectTotalEvaluationByBookId(bookid);
			logger.log(Level.INFO, "totalevaluation = " + totalevaluation + ".");
			logger.log(Level.INFO, "Success to calcurate totalevaluation.");

			// bookidに対応するBookのtotalevaluationを更新
			logger.log(Level.INFO, "Create PostBook instance.");
			PostBook postBook = new PostBook();
			postBook.setUser(user);
			postBook.setTotalevaluation(totalevaluation);
			
			// Book更新API実行メソッド
			logger.log(Level.INFO, "Update Book totalevaluation.(bookid = " + bookid + ")");
			responseBook = postBookApi(user, bookid, postBook);
			logger.log(Level.INFO, "Success to update Book.");
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			// throw new ResponseStatusException(e.getStatusCode()); // restControllerのレスポンスコードとして400系をthrow
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request."); // restControllerのレスポンスコードとして400をthrow
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			// throw new ResponseStatusException(e.getStatusCode()); // restControllerのレスポンスコードとして500系をthrow
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error."); // restControllerのレスポンスコードとして500をthrow
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		}
	}
	
	// Review全件削除API
	// 指定したbookidに紐付くReviewを全件削除
	@DeleteMapping("/api/review/all")
	public void deleteAllReview(@RequestParam(value="user", required=false) String user, @RequestParam(value="bookid", required=true) int bookid) {
		try {
			logger.log(Level.INFO, "DELETE /api/review/all?user=" + user + "&bookid=" + bookid);

			// Bookに紐付くReviewを全件削除
			logger.log(Level.INFO, "Delete all Review.(bookid = " + bookid + ")");
			logger.log(Level.FINE, "reviewService.deleteAllByBookId(" + bookid + ")");
			reviewService.deleteAllByBookId(bookid); 
			logger.log(Level.INFO, "Success to delete all Review.(bookid = " + bookid + ")");
			
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		}
	}
	
	// TotalEvaluation取得API（今は使っていない）
	// 対象のbookidを配列で受け取る
	@GetMapping("/api/review/totalevaluation")
	public Iterable<TotalEvaluation> getTotalEvaluation(@RequestParam(value="user", required=false) String user, @RequestParam(value="page", required=false, defaultValue = "0") int page, @RequestParam(value="bookids[]", required=false) List<Integer> bookids){
		try {
			if(bookids == null) { // bookidがパラメータで指定されていない場合
				if (page != 0 ) { // TotalEvaluationが高い順に並べてページ分割した際の指定されたpageの結果を返す
					logger.log(Level.INFO, "GET /api/review/tolalevaluation?user=" + user + "&page=" + page);
					return reviewService.selectTotalEvaluationDescByLimitOffset(page, PAGE_SIZE);
				} else { // 上位N件を返す
					logger.log(Level.INFO, "GET /api/review/tolalevaluation?user=" + user);
					return reviewService.selectTotalEvaluationTopN(TOP_NUMBER);
				}
			} else { // bookidが指定されたら該当するTotalEvaluationを返す
				logger.log(Level.INFO, "GET /api/review/tolalevaluation?user=" + user + "&bookid[]=" + bookids);
				return reviewService.selectTotalEvaluationByBookIds(bookids);
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		}
	}
	
	// -------------------
	
	// Book取得API実行メソッド
	// [Book API] GET /api/book/{bookid}
	ResponseEntity<Book> getBookApi(String user, int bookid) {
		String bookRequestUrl = BOOK_API_URL + "/" + bookid + "?user=" + user;
	
		try {
			// Book取得API
			logger.log(Level.INFO, "[Book API] GET " + bookRequestUrl);
			ResponseEntity<Book> responseBook = restTemplate.exchange(bookRequestUrl, HttpMethod.GET, null, Book.class);
			logger.log(Level.INFO, "[Book API] Book has returned from Book API.");
			
			return responseBook;
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch  HttpClientErrorException");
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch HttpServerErrorException");
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "[Book API] Catch Exception");
			logger.log(Level.SEVERE, "[Book API] Unable access to " + "GET " + bookRequestUrl);
			throw e;
		}
	}
	
	// Book更新API実行メソッド
	// [Book API] POST /api/book/{bookid}/update
	ResponseEntity<Book> postBookApi(String user, int bookid, PostBook postBook) {
		String bookRequestUrl = BOOK_API_URL + "/" + bookid + "/update";
		
		try {
			// Book更新API実行
			HttpEntity<PostBook> entity = new HttpEntity<>(postBook, null);
			
			logger.log(Level.INFO, "[Book API] POST " + bookRequestUrl);
			ResponseEntity<Book> responseBook = restTemplate.exchange(bookRequestUrl, HttpMethod.POST, entity, Book.class);
			logger.log(Level.INFO, "[Book API] Book has returned from Book API.");
			
			return responseBook;
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch  HttpClientErrorException");
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch HttpServerErrorException");
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "[Book API] Catch Exception");
			logger.log(Level.SEVERE, "[Book API] Unable access to " + "POST " + bookRequestUrl);
			throw e;
		}
	}
	
}
