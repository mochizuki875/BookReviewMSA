package com.example.demo.controller;

import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.demo.entity.BookList;
import com.example.demo.entity.PostBook;
import com.example.demo.service.BookService;

//REST APIを示すアノテーション（戻り値がViewではなくJson/XML）
@RestController
public class BookAPIController {
	
	final int TOP_NUMBER = 10; // トップページの表示件数
	final int PAGE_SIZE = 10; // 1ページあたりの表示件数
	
	final String REVIEW_API_URL = "http://127.0.0.1:8083/api/review"; // Review APIのURL
	
	// RestTemplateインスタンス作成
	RestTemplate restTemplate = new RestTemplate();
	
	// Serviceインスタンスを作成
	@Autowired
	BookService bookService;
	
	Logger logger = Logger.getLogger(BookAPIController.class.getName());
	ConsoleHandler handler = new ConsoleHandler();
	
	// Book一覧取得API
	// 登録されているBook一覧を取得する
	@GetMapping("/api/book")
	public BookList getBookList(@RequestParam(value="user", required=false) String user, @RequestParam(value="page", required=false, defaultValue = "0") int page) {
		try {
			BookList bookList = new BookList(); // BookListインスタンスを作成
			
			if (page!=0) { // pageが指定されている場合 
				// 指定されたpageのBook一覧を取得する
				logger.log(Level.INFO, "GET /api/book?user=" + user + "&page=" + page); 
				bookList.setPage(page); // BookListにpageを格納
				
				int allPages = bookService.countAllPages(PAGE_SIZE); // 全ページ数を取得
				bookList.setAllPages(allPages); // BookListにallPageを格納
				
				logger.log(Level.INFO, "Get BookList of page " + page + "/" + allPages + ".(PAGE_SIZE=" + PAGE_SIZE + ")");
				logger.log(Level.FINE, "bookService.selectAllDescByPage(" + page + ", " + allPages + ")");
				Iterable<Book> bookListPage = bookService.selectAllDescByPage(page, PAGE_SIZE); // 指定したページのBook情報一覧を取得
				logger.log(Level.INFO, "Success to get BookList.");
				
				bookList.setBookListPage(bookListPage); // BookListにbookListPageを格納
				
			} else { // pageが指定されていない or page=0の場合
				// 評価の高い上位TOP_NUMBER件のBookを取得する
				logger.log(Level.INFO, "GET /api/book?user=" + user);
				logger.log(Level.INFO, "Get BookList of top " + TOP_NUMBER + ".");
				logger.log(Level.FINE, "bookService.selectTopN(" + TOP_NUMBER + ")");
				Iterable<Book> bookListPage = bookService.selectTopN(TOP_NUMBER); // 登録されているBookのうち上位TOP_NUMBER件のBookを件取得
				logger.log(Level.INFO, "Success to get BookList.");
				
				bookList.setPage(1); // bookListPageにpageを格納
				bookList.setAllPages(1); // bookListPageにallPageを格納
				bookList.setBookListPage(bookListPage); // bookListPageにbookListPageを格納
				
			}
			logger.log(Level.INFO, "Return BookList.");
			return bookList;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		}
	}
	
	// Book検索API
	// Book一覧をkeyword検索し結果一覧を取得するAPI
	// 検索ワードをリクエストパラメータとして受け取って検索結果を返す（page件単位でページ分割した際の指定されたページ分）
	@GetMapping("/api/book/search")
	public BookList searchBook(@RequestParam(value="user", required=false) String user, @RequestParam(value="keyword", required=false) String keyword, @RequestParam(value="page", defaultValue = "1") int page) {
		try {
			logger.log(Level.INFO, "GET /api/book/search?user=" + user + "&keyword=" + keyword + "&page=" + page);
			BookList bookList = new BookList();
			
			if(keyword == null | keyword == "") { // keywordが指定されていない場合
				// 評価の高い上位TOP_NUMBER件のBookを取得する
				logger.log(Level.INFO, "keyword=" + keyword);
				
				logger.log(Level.INFO, "Get BookList of top " + TOP_NUMBER + ".");
				logger.log(Level.FINE, "bookService.selectTopN(" + TOP_NUMBER + ")");
				Iterable<Book> bookListPage = bookService.selectTopN(TOP_NUMBER); // Book情報のうち上位TOP_NUMBER件を取得する
				logger.log(Level.INFO, "Success to get BookList.");
				
				bookList.setPage(1);
				bookList.setAllPages(1);
				bookList.setBookListPage(bookListPage);
				
			} else { // keywordが指定されている場合は検索結果のうちpage/PAGE_SIZEのBook一覧を取得する
				logger.log(Level.INFO, "keyword=" + keyword);
				
				bookList.setPage(page);
				
				int allPages = bookService.countSearchAllPages(keyword, PAGE_SIZE); // 検索結果の全ページ数を取得
				bookList.setAllPages(allPages);
				
				logger.log(Level.INFO, "Search keyword = " + keyword + " and get BookList of page " + page + "/" + allPages  + ".(PAGE_SIZE = " + PAGE_SIZE + ")");
				logger.log(Level.FINE, "bookService.searchAllDescByPage(" + keyword + ", " +  page + ", " +  PAGE_SIZE + ")");
				Iterable<Book> bookListPage = bookService.searchAllDescByPage(keyword, page, PAGE_SIZE); // keyword検索結果のうち指定したページのBook情報一覧を取得
				logger.log(Level.INFO, "Success to get BookList.");
				
				bookList.setBookListPage(bookListPage);
				
			}
			logger.log(Level.INFO, "Return BookList.");
			return bookList;

		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		}
	}
	
	// Book取得API
	// bookidをリクエストパラメータとして受け取ってBookを返す
	@GetMapping("/api/book/{bookid}")
	public Book getBook(@RequestParam(value="user", required=false) String user, @PathVariable int bookid) {
		try {
			logger.log(Level.INFO, "GET /api/book/" + bookid + "?user=" + user);
			
			logger.log(Level.INFO, "Get Book.(bookid = " + bookid + ")");
			logger.log(Level.FINE, "bookService.selectOneById(" + bookid + ")");
			Optional<Book> bookOpt = bookService.selectOneById(bookid); // bookidからBookを取得
			if(bookOpt.isPresent()) {
				Book book = bookOpt.get();
				logger.log(Level.INFO, "Success to get Book.(bookid = " + bookid + ")");
				
				logger.log(Level.INFO, "Return Book.");
				return book;
			}
			
			// bookidに対応するBookがなければ404エラー
			logger.log(Level.SEVERE, "Faild to get Book.(bookid = " + bookid + ")");
			logger.log(Level.SEVERE, "throw  HttpClientErrorException");
		    throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found");
			
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw new ResponseStatusException(e.getStatusCode()); // restControllerのレスポンスコードとしてthrow
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw new ResponseStatusException(e.getStatusCode()); // restControllerのレスポンスコードとしてthrow
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		}
	}
	
	// Book新規登録API
	@PostMapping("/api/book/insert")
	// POSTでapplication/jsonでリクエストボディを受信するには@RequestParamではなく@RequestBody
	public Book insertBook(@RequestBody PostBook postBook) {
		try {
			logger.log(Level.INFO, "POST /book/insert");
			logger.log(Level.INFO, " user: " + postBook.getUser());
			logger.log(Level.INFO, " title: " + postBook.getTitle());
			logger.log(Level.INFO, " overview: " + postBook.getOverview());
			
			logger.log(Level.INFO, "Create Book instance.");
			Book book = new Book();
			book.setTitle(postBook.getTitle());
			book.setOverview(postBook.getOverview());
			
			logger.log(Level.INFO, "Insert Book.");
			logger.log(Level.FINE, "bookService.insertOne(" + book + ")");
			book = bookService.insertOne(book); // Bookの新規登録
			logger.log(Level.INFO, "Success to insert Book.");
			
			logger.log(Level.INFO, "Return Book.(bookid = " + book.getId() + ")");
			return book;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		} 
	}
	
	// Book更新API
	@PostMapping("/api/book/{bookid}/update")
	public Book updateBook(@RequestBody PostBook postBook, @PathVariable(value="bookid", required=true) int bookid) {		
		try {
			logger.log(Level.INFO, "POST /book/" + bookid + "/update");
			logger.log(Level.INFO, " user: " + postBook.getUser());
			logger.log(Level.INFO, " title: " + postBook.getTitle());
			logger.log(Level.INFO, " overview: " + postBook.getOverview());
			logger.log(Level.INFO, " totalevaluation: " + postBook.getTotalevaluation());
			
			
			logger.log(Level.INFO, "Get book.(bookid = " + bookid + ")");
			logger.log(Level.FINE, "bookService.selectOneById(" + bookid + ")");
			Optional<Book> bookOpt = bookService.selectOneById(bookid); // bookidから本の詳細情報を取得
			if(bookOpt.isPresent()) {
				logger.log(Level.INFO, "Success to get Book.(bookid = " + bookid + ")");
				Book book = bookOpt.get();
				if (postBook.getTitle() != null) {
					logger.log(Level.INFO, " Set title: " + postBook.getTitle());
					book.setTitle(postBook.getTitle());
				}
				if (postBook.getOverview() != null) {
					logger.log(Level.INFO, " Set overview: " + postBook.getOverview());
					book.setOverview(postBook.getOverview());
				}
				if (postBook.getTotalevaluation() != 0.0) {
					logger.log(Level.INFO, " Set totalevaluation: " + postBook.getTotalevaluation());
					book.setTotalevaluation(postBook.getTotalevaluation());
				}
				
				logger.log(Level.INFO, "Update Book.(bookid = " + book.getId() + ")");
				book = bookService.updateOne(book); // Bookの更新
				logger.log(Level.INFO, "Success to updated Book.(bookid = " + book.getId() + ")");
				
				logger.log(Level.INFO, "Return Book.(bookid = " + book.getId() + ")");
				return book;
			}
				// bookidに対応するBookがなければ404エラー
				logger.log(Level.SEVERE, "Faild to get Book.(bookid = " + bookid + ")");
				logger.log(Level.SEVERE, "throw  HttpClientErrorException");
			    throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not Found");
				
			}
			catch (HttpClientErrorException e) {
				logger.log(Level.SEVERE, "Catch  HttpClientErrorException");
				logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
				throw new ResponseStatusException(e.getStatusCode()); // restControllerのレスポンスコードとしてthrow
			}
			catch (HttpServerErrorException e) {
				logger.log(Level.SEVERE, "Catch HttpServerErrorException");
				logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
				throw new ResponseStatusException(e.getStatusCode()); // restControllerのレスポンスコードとしてthrow
			}
			catch (Exception e) {
				logger.log(Level.SEVERE, "Catch Exception");
				logger.log(Level.SEVERE, "Internal Server Error");
				throw e;
			}
	}
	
	// Book削除API
	@DeleteMapping("/api/book/{bookid}")
	public void deleteReview(@RequestParam(value="user", required=false) String user, @PathVariable int bookid) {
		try {
			logger.log(Level.INFO, "DELETE /book/" + bookid + "?user=" + user);

			// Review全件削除API
			logger.log(Level.INFO, "Delete all Review related to bookid =" + bookid + ".");
			deleteReviewAllApi(user,bookid);
			logger.log(Level.INFO, "Success to delete Review.(bookid = " + bookid + ")");
			
			logger.log(Level.INFO, "Delete Book. (bookid = " + bookid + ")");
			logger.log(Level.FINE, "bookService.deleteOneById(" + bookid + ")");
			bookService.deleteOneById(bookid); // Bookを削除
			logger.log(Level.INFO, "Success to delete Book.(bookid = " + bookid + ")");
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
	
	// Review全件削除API実行メソッド
	// [Review API] SELETE /api/review/all
	void deleteReviewAllApi(String user, int bookid) {
		String reviewRequestUrl = REVIEW_API_URL + "/all?user=" + user + "&bookid=" + bookid;
	
		try {
			// ReviewAPI
			logger.log(Level.INFO, "[Review API] DELETE " + reviewRequestUrl);
			ResponseEntity<Book> responseBook = restTemplate.exchange(reviewRequestUrl, HttpMethod.DELETE, null, Book.class);
			logger.log(Level.INFO, "[Review API] All reviews related to bookid = " + bookid + " has deleted.");
			
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "[Review API] Catch  HttpClientErrorException");
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "[Review API] Catch HttpServerErrorException");
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "[Review API] Catch Exception");
			logger.log(Level.SEVERE, "[Review API] Unable access to " + "DELETE " + reviewRequestUrl);
			throw e;
		}
	
	}
	
}
