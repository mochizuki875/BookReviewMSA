package com.example.demo.controller;

import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookDetail;
import com.example.demo.entity.BookList;
import com.example.demo.entity.PostBook;
import com.example.demo.entity.PostReview;
import com.example.demo.entity.Review;
import com.example.demo.service.BookService;
import com.example.demo.service.ReviewService;


//REST APIを示すアノテーション（戻り値がViewではなくJson/XML）
@RestController
public class BookReviewAPIController {
	
	final int TOP_NUMBER = 10; // トップページの表示件数
	final int PAGE_SIZE = 10; // 1ページあたりの表示件数
	
	// Serviceインスタンスを作成
	@Autowired
	BookService bookService;
	@Autowired
	ReviewService reviewService;
	
	Logger logger = Logger.getLogger(BookReviewAPIController.class.getName());
	ConsoleHandler handler = new ConsoleHandler();
	
	// Book一覧取得API
	// 登録されているBook一覧を取得するAPI
	@GetMapping("/api/book")
	public BookList getBook(@RequestParam(value="user", required=false) String user, @RequestParam(value="page", required=false, defaultValue = "0") int page) {
		try {
			BookList bookList = new BookList(); // レスポンス型インスタンスを作成
			
			if (page!=0) { // 指定されたpageのBook一覧を取得する
				logger.log(Level.INFO, "GET /api/book?user=" + user + "&page=" + page); 
				bookList.setPage(page); // レスポンス用インスタンスにpageを格納
				
				int allPages = bookService.countAllPages(PAGE_SIZE); // 全ページ数を取得
				bookList.setAllPages(allPages); // レスポンス用インスタンスにallPageを格納
				
				logger.log(Level.INFO, "Get bookList of page " + page + "/" + allPages + ".(PAGE_SIZE=" + PAGE_SIZE + ")");
				logger.log(Level.FINE, "bookService.selectAllDescByPage(" + page + ", " + allPages + ")");
				
				Iterable<Book> bookListPage = bookService.selectAllDescByPage(page, PAGE_SIZE); // 指定したページのBook情報一覧を取得
				bookList.setBookListPage(bookListPage);
				
				logger.log(Level.INFO, "return bookList.");
				return bookList;
				
			} else { // pageが指定されていない or page=0の場合
				// 評価の高い上位TOP_NUMBER件のBookを取得する
				logger.log(Level.INFO, "GET /api/book?user=" + user);
				logger.log(Level.INFO, "Get bookList of top " + TOP_NUMBER + ".");
				logger.log(Level.FINE, "bookService.selectTopN(" + TOP_NUMBER + ")");
				
				Iterable<Book> bookListPage = bookService.selectTopN(TOP_NUMBER); // 登録されているBookのうち上位TOP_NUMBER件のBookを件取得
				
				bookList.setPage(1); // レスポンス用インスタンスにpageを格納
				bookList.setAllPages(1); // レスポンス用インスタンスにallPageを格納
				bookList.setBookListPage(bookListPage); // レスポンス用インスタンスにbookListPageを格納
				
				logger.log(Level.INFO, "return bookList.");
				
				return bookList;
			}
		}
		catch (HttpClientErrorException e) {
			throw e;
		}
		catch (HttpServerErrorException e) {
			throw e;
		} 
	}
	
	// Book検索API
	// Book一覧をkeyword検索し結果一覧を取得するAPI
	// 検索ワードをリクエストパラメータとして受け取って検索結果を返す（page件単位でページ分割した際の指定されたページ分）
	@GetMapping("/api/book/search")
	public BookList searchBook(@RequestParam(value="user", required=false) String user, @RequestParam(value="keyword", required=false) String keyword, @RequestParam(value="page", defaultValue = "1") int page) {
		try { // keywordが指定されていない場合は上位TOP_NUMBER件のBook一覧を取得する
			logger.log(Level.INFO, "GET /api/book/search?user=" + user + "&keyword=" + keyword + "&page=" + page);
			BookList bookList = new BookList();
			
			if(keyword == null | keyword == "") { 
				logger.log(Level.INFO, "return home.");
				Iterable<Book> bookListPage = bookService.selectTopN(TOP_NUMBER); // Book情報のうち上位TOP_NUMBER件を件取得
				bookList.setPage(1);
				bookList.setAllPages(1);
				bookList.setBookListPage(bookListPage);
				
				logger.log(Level.INFO, "Get bookList of top " + TOP_NUMBER + ".");
				logger.log(Level.FINE, "bookService.selectTopN(" + TOP_NUMBER + ")");
				
				return bookList;
			} else { // keywordが指定されている場合は検索結果のうちpage/PAGE_SIZEのBook一覧を取得する
				bookList.setPage(page);
				
				int allPages = bookService.countSearchAllPages(keyword, PAGE_SIZE); // 検索結果の全ページ数を取得
				bookList.setAllPages(allPages);
				
				logger.log(Level.INFO, "Search keyword=" + keyword + " and get bookList of page " + page + "/" + allPages  + ".(PAGE_SIZE=" + PAGE_SIZE + ")");
				logger.log(Level.FINE, "bookService.searchAllDescByPage(" + keyword + ", " +  page + ", " +  PAGE_SIZE + ")");
				
				Iterable<Book> bookListPage = bookService.searchAllDescByPage(keyword, page, PAGE_SIZE); // keyword検索結果のうち指定したページのBook情報一覧を取得
				bookList.setBookListPage(bookListPage);
				
				logger.log(Level.INFO, "return bookList.");
				return bookList;
			}
		}
		catch (HttpClientErrorException e) {
			throw e;
		}
		catch (HttpServerErrorException e) {
			throw e;
		} 
	}
	
	// Book詳細取得API
	// bookidをリクエストパラメータとして受け取って本の詳細ページを返す
	@GetMapping("/api/book/{bookid}/detail")
	public BookDetail getBookDetail(@RequestParam(value="user", required=false) String user, @PathVariable int bookid) {
		try {
			logger.log(Level.INFO, "GET /api/book/" + bookid + "/detail?user=" + user);
			
			logger.log(Level.INFO, "Get book detail.");
			logger.log(Level.FINE, "bookService.selectOneById(" + bookid + ")");
			
			BookDetail bookDetail = new BookDetail();
			
			Optional<Book> bookOpt = bookService.selectOneById(bookid); // bookidからBookの詳細情報を取得
			if(bookOpt.isPresent()) {
				logger.log(Level.FINE, "bookOpt.isPresent()=true");
				bookDetail.setBook(bookOpt.get());
			}
			
			logger.log(Level.INFO, "Get book reviews.");
			logger.log(Level.FINE, "reviewService.selectAllByBookId(" + bookid + ")");
			
			// Review APIを呼んで取得するようにしたい
			Iterable<Review> reviewList = reviewService.selectAllByBookId(bookid); // 本のRVを取得
			bookDetail.setReviewList(reviewList);
			
			logger.log(Level.INFO, "return book detail.");
			return bookDetail;
		}
		catch (HttpClientErrorException e) {
			throw e;
		}
		catch (HttpServerErrorException e) {
			throw e;
		} 
	}
	
	// Book新規登録API
	@PostMapping("/api/book/insert")
	// POSTでapplication/jsonでリクエストボディを受信するには@RequestParamではなく@RequestBody
	public Book insert(@RequestBody PostBook postBook) {
		try {
			logger.log(Level.INFO, "POST /book/insert");
			logger.log(Level.INFO, "user: " + postBook.getUser());
			logger.log(Level.INFO, "title: " + postBook.getTitle());
			logger.log(Level.INFO, "overview: " + postBook.getOverview());
			
			logger.log(Level.INFO, "Create book instance.");
			Book book = new Book();
			book.setTitle(postBook.getTitle());
			book.setOverview(postBook.getOverview());
			
			logger.log(Level.INFO, "Insert book.");
			logger.log(Level.FINE, "bookService.insertOne(" + book + ")");
			
			book = bookService.insertOne(book); // Bookの新規登録
			
			logger.log(Level.INFO, "return book user:" + postBook.getUser() + " bookid: " + book.getId());
			
			return book;
		}
		catch (HttpClientErrorException e) {
			throw e;
		}
		catch (HttpServerErrorException e) {
			throw e;
		} 
	}
	
	// Book更新API
	@PostMapping("/api/book/{bookid}/update")
	public Book update(@RequestBody PostBook postBook, @PathVariable(value="bookid", required=true) int bookid) {		
		try {
			logger.log(Level.INFO, "POST /book/" + bookid + "/update");
			logger.log(Level.INFO, "user: " + postBook.getUser());
			logger.log(Level.INFO, "title: " + postBook.getTitle());
			logger.log(Level.INFO, "overview: " + postBook.getOverview());
			
			Book book = new Book();
			
			logger.log(Level.INFO, "Get book.");
			logger.log(Level.FINE, "bookService.selectOneById(" + bookid + ")");
			
			Optional<Book> bookOpt = bookService.selectOneById(bookid); // bookidから本の詳細情報を取得
			if(bookOpt.isPresent()) {
				logger.log(Level.INFO, "Update book.");
				
				book = bookOpt.get();
				book.setTitle(postBook.getTitle());
				book.setOverview(postBook.getOverview());
				book = bookService.updateOne(book); // Bookの更新
				
				logger.log(Level.INFO, "bookid=" + book.getId() + " is updated.");
			}
			
			logger.log(Level.INFO, "return book user:" + postBook.getUser() + " bookid: " + book.getId());
			
			return book;
		}
		catch (HttpClientErrorException e) {
			throw e;
		}
		catch (HttpServerErrorException e) {
			throw e;
		} 
	}
	
	// Book削除API
	@DeleteMapping("/api/book/{bookid}")
	public void deleteReview(@RequestParam(value="user", required=false) String user, @PathVariable int bookid) {
		try {
			logger.log(Level.INFO, "DELETE /book/" + bookid);
			logger.log(Level.INFO, "user: " + user);
			
			logger.log(Level.INFO, "Delete all reviews related to bookid =" + bookid + ".");
			logger.log(Level.FINE, "reviewService.deleteAllByBookId(" + bookid + ")");
			reviewService.deleteAllByBookId(bookid); // Bookに紐付くReviewを全件削除
			
			logger.log(Level.INFO, "Delete book bookid =" + bookid + ".");
			logger.log(Level.FINE, "bookService.deleteOneById(" + bookid + ")");
			bookService.deleteOneById(bookid); // Bookを削除
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
			
			logger.log(Level.INFO, "Create Review instance.");
			Review review = new Review();
			review.setEvaluation(postReview.getEvaluation());
			review.setContent(postReview.getContent());
			review.setBookid(postReview.getBookid());
			review.setUserid(postReview.getUserid());
			
			logger.log(Level.INFO, "Insert review.");
			logger.log(Level.FINE, "reviewService.insertOne(" + review + ")");
			reviewService.insertOne(review); // Reviewを登録
			
			logger.log(Level.INFO, "Get reviews of bookid=" + review.getBookid() + ".");
			logger.log(Level.FINE, "reviewService.selectAllByBookId(" + review.getBookid() + ")");
			Iterable<Review> reviewList = reviewService.selectAllByBookId(review.getBookid()); // 対象BookのReviewを全て取得
			
			// 取得したReviewのevaluationの平均値を算出
			double totalEvaluation = 0.0;
			int counter = 0;
			for(Review tempReview : reviewList) {
				totalEvaluation += tempReview.getEvaluation();
				counter ++;
			}
			
			logger.log(Level.INFO, "Set totalEvaluation.");
			logger.log(Level.FINE, "bookService.updateTotalevaluationById(" + review.getBookid() + ", " + (double)Math.round(totalEvaluation*10/counter)/10 + ")");
			bookService.updateTotalevaluationById(review.getBookid(), (double)Math.round(totalEvaluation*10/counter)/10); // 対象Bookのtotalevaluationを更新
			
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
	@DeleteMapping("/api/book/{bookid}/review/{reviewid}")
	public void deleteReview(@RequestParam(value="user", required=false) String user, @PathVariable int bookid, @PathVariable int reviewid) {
		try {
			logger.log(Level.INFO, "DELETE /api/book/" + bookid + "/review/" + reviewid);
			logger.log(Level.INFO, "user: " + user);
			
			logger.log(Level.INFO, "Delete review of reviewid=" + reviewid + ".");
			logger.log(Level.FINE, "reviewService.deleteOneById(" + reviewid + ")");
			reviewService.deleteOneById(reviewid); // idを指定してReviewを削除
		}
		catch (HttpClientErrorException e) {
			throw e;
		}
		catch (HttpServerErrorException e) {
			throw e;
		} 
	}	
}
