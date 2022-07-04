package com.example.demo.controller;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.entity.Book;
import com.example.demo.entity.BookList;
import com.example.demo.entity.PostBook;
import com.example.demo.entity.PostReview;
import com.example.demo.entity.Review;
import com.example.demo.entity.ReviewList;

@Controller
@RequestMapping
public class BookReviewBFFController {
	final String BOOK_API_URL = "http://127.0.0.1:8082/api/book"; // Book APIのURL
	final String REVIEW_API_URL = "http://127.0.0.1:8083/api/review"; // Review APIのURL

	String bookRequestUrl;
	String reviewRequestUrl;
	
	// RestTemplate作成
	RestTemplate restTemplate = new RestTemplate();
	
	// Logger作成
	Logger logger = Logger.getLogger(BookReviewBFFController.class.getName());
	ConsoleHandler handler = new ConsoleHandler();
	
	// Book一覧表示
	@GetMapping("/")
	public String showHome(@RequestParam(value="user", required=false) String user, Model model) {
		try {
			logger.log(Level.INFO, "GET /?user=" + user);
			
			model.addAttribute("user", user); // userをModelに格納
			
			int showFlag = 0; // Book表示フラグ
			model.addAttribute("showFlag", showFlag); // showFlagをModelに格納
			
			try {
				logger.log(Level.INFO, "Get BookList.");
				ResponseEntity<BookList> responseBook = getBookListApi(user); // Book一覧取得API実行
				logger.log(Level.INFO, "BookList has returned.");
				
				model.addAttribute("bookList", responseBook.getBody().getBookListPage()); // bookListをModelに格納
			}
			catch(Exception e) {
				// エラーでも画面表示できるように空のレスポンスを返す
				BookList bookList = new BookList();
				ResponseEntity<BookList> responseBook = new ResponseEntity<BookList>(bookList, HttpStatus.INTERNAL_SERVER_ERROR);
				model.addAttribute("bookList", responseBook.getBody().getBookListPage()); // bookListをModelに格納
			}
			
			logger.log(Level.INFO, "Return home page and show bookList.");
			return "home";
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		}
	}
	
	// Book表示（page件単位でページ分割した際の指定されたページ分）
	@GetMapping("/book")
	public String showAllBook(@RequestParam(value="user", required=false) String user, @RequestParam(value="page", defaultValue = "1") int page, Model model) {
		try {
			logger.log(Level.INFO, "GET /book?user=" + user + "&page=" + page);
			
			model.addAttribute("user", user); // userをModelに格納
			
			int showFlag = 1; // Book表示フラグ
			model.addAttribute("showFlag", showFlag); // showFlagをModelに格納
			
			try {
				logger.log(Level.INFO, "Get BookList.(page = " + page + ")");
				ResponseEntity<BookList> responseBook = getBookListPageApi(user, page); // Book一覧取得API実行
				logger.log(Level.INFO, "BookList has returned.(page = " + page + ")");
				
				model.addAttribute("bookList", responseBook.getBody().getBookListPage()); // bookListをModelに格納
				model.addAttribute("page", responseBook.getBody().getPage()); //  pageをModelに格納
				model.addAttribute("allPages", responseBook.getBody().getAllPages()); //  allPagesをModelに格納
			}
			catch (Exception e) {
				// エラーでも画面表示できるように空のレスポンスを返す
				BookList bookList = new BookList();
				ResponseEntity<BookList> responseBook = new ResponseEntity<BookList>(bookList, HttpStatus.INTERNAL_SERVER_ERROR);
				
				model.addAttribute("bookList", responseBook.getBody().getBookListPage()); // bookListをModelに格納
				model.addAttribute("page", responseBook.getBody().getPage()); //  pageをModelに格納
				model.addAttribute("allPages", responseBook.getBody().getAllPages()); //  allPagesをModelに格納
			}
			
			logger.log(Level.INFO, "Return home page and show bookList. (page = " + page + ")");
			return "home";
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		}
	}	
	
	// Book検索
	// 検索ワードをリクエストパラメータとして受け取って検索結果を返す（page件単位でページ分割した際の指定されたページ分）
	@GetMapping("/book/search")
	public String searchBook(@RequestParam(value="user", required=false) String user, @RequestParam(value="keyword", required=false) String keyword, @RequestParam(value="page", defaultValue = "1") int page, RedirectAttributes redirectAttributes, Model model) {
		try {
			logger.log(Level.INFO, "GET /book/search?user=" + user + "&keyword=" + keyword + "&page=" + page);
			
			if(keyword == "") { // keywordが指定されていない場合はトップページに戻る
				logger.log(Level.INFO, "Redirect to /?user=" + user + ".(keyword = null)");
				return "redirect:/" + "?user=" + user;
				
			} else { // keywordが指定されていれば検索処理実行
			
				model.addAttribute("user", user); // userをModelに格納
				model.addAttribute("keyword", keyword); // keywordをModelに格納
						
				int showFlag = 2; // Book表示フラグ
				model.addAttribute("showFlag", showFlag); // showFlagをModelに格納
		
				try {
					logger.log(Level.INFO, "Search keyword=" + keyword + " and get BookList.(keyword = " + keyword + ", page = " + page + ")");
					ResponseEntity<BookList> responseBook = searchBookListPageApi(user, keyword, page); // Book検索API実行メソッド
					logger.log(Level.INFO, "BookList has returned.(page = " + page + ")");
					
					model.addAttribute("bookList", responseBook.getBody().getBookListPage()); // bookListをModelに格納
					model.addAttribute("page", responseBook.getBody().getPage()); //  pageをModelに格納
					model.addAttribute("allPages", responseBook.getBody().getAllPages()); //  allPagesをModelに格納
				}
				catch (Exception e) {
					// エラーでも画面表示できるように空のレスポンスを返す
					BookList bookList = new BookList();
					ResponseEntity<BookList> responseBook = new ResponseEntity<BookList>(bookList, HttpStatus.INTERNAL_SERVER_ERROR);
					
					model.addAttribute("bookList", responseBook.getBody().getBookListPage()); // bookListをModelに格納
					model.addAttribute("page", responseBook.getBody().getPage()); //  pageをModelに格納
					model.addAttribute("allPages", responseBook.getBody().getAllPages()); //  allPagesをModelに格納
				}
				logger.log(Level.INFO, "Return home page and show BookList. (keyword = " + keyword + ", page = " + page + ")");
				return "home";
			}
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		}
	}
	
	// Book詳細画面表示
	// bookidをリクエストパラメータとして受け取ってBook詳細ページを返す
	@GetMapping("/book/{bookid}")
	public String showBook(@RequestParam(value="user", required=false) String user, @PathVariable int bookid, Model model) {
		try {
			logger.log(Level.INFO, "GET /book/" + bookid + "?user=" + user);
			
			model.addAttribute("user", user); // userをModelに格納
			
			try { // Book取得API
				logger.log(Level.INFO, "Get Book.(bookid = " + bookid + ")");
				ResponseEntity<Book> responseBook = getBookApi(user, bookid);// Book取得API実行メソッド
				logger.log(Level.INFO, "Book has returned.(bookid = " + bookid + ")");
				model.addAttribute("book", responseBook.getBody()); // bookをModelに格納
			}
			catch (Exception e) {
				// エラーでも画面表示できるように空のレスポンスを返す
				Book book = new Book();
				ResponseEntity<Book> responseBook = new ResponseEntity<Book>(book, HttpStatus.INTERNAL_SERVER_ERROR);
				model.addAttribute("book", responseBook.getBody()); // bookをModelに格納
			}
		
			try { // ReviewList取得API
				logger.log(Level.INFO, "Get ReviewList.(bookid = " + bookid + ")");
				ResponseEntity<ReviewList> responseReview = getReviewList(user, bookid);// ReviewList取得API実行メソッド
				logger.log(Level.INFO, "ReviewList has returned.(bookid = " + bookid + ")");
				model.addAttribute("reviewList", responseReview.getBody().getReviewListPage()); // reviewListをModelに格納
			}
			catch (Exception e) {
				// エラーでも画面表示できるように空のレスポンスを返す
				ReviewList reviewList = new ReviewList();
				ResponseEntity<ReviewList> responseReview = new ResponseEntity<ReviewList>(reviewList, HttpStatus.INTERNAL_SERVER_ERROR);
				model.addAttribute("reviewList", responseReview.getBody().getReviewListPage());
			}
			
			logger.log(Level.INFO, "Return Book page and show ReviewList. (bookid = " + bookid + ")");
			return "detail";
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		} 
	}
	
	// Book新規登録画面
	@GetMapping("/book/newbook")
	public String newBook(@RequestParam(value="user", required=false) String user, Model model) {
		try {
			logger.log(Level.INFO, "GET /book/newbook?user=" + user);
			
			model.addAttribute("user", user); // userをModelに格納
			
			boolean editFlag = false; // 編集フラグ（false:新規, true:編集）
			model.addAttribute("editFlag", editFlag); // editFlagをModelに格納
			
			logger.log(Level.INFO, "Return editbook.(editFlag =" + editFlag + ")");
			return "editbook";
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		} 
	}
	
	// Book編集画面
	@GetMapping("/book/{bookid}/edit")
	public String editBook(@RequestParam(value="user", required=false) String user, @PathVariable(value="bookid", required=true) int bookid, Model model) {
		try {
			logger.log(Level.INFO, "GET /book/" + bookid + "/edit?user=" + user);
			
			model.addAttribute("user", user); // userをModelに格納
			
			boolean editFlag = true; // 編集フラグ（false:新規, true:編集）
			model.addAttribute("editFlag", editFlag); // editFlagをModelに格納
			
			try {
				logger.log(Level.INFO, "Get Book.(bookid = " + bookid + ")");
				ResponseEntity<Book> responseBook = getBookApi(user, bookid);// Book取得API実行メソッド
				logger.log(Level.INFO, "Book has returned.(bookid = " + bookid + ")");
				model.addAttribute("book", responseBook.getBody()); // bookをModelに格納	
			}
			catch(Exception e) {
				// エラーでも画面表示できるように空のレスポンスを返す
				Book book = new Book();
				ResponseEntity<Book> responseBook = new ResponseEntity<Book>(book, HttpStatus.INTERNAL_SERVER_ERROR);
				model.addAttribute("book", responseBook.getBody()); // bookをModelに格納
			}
			
			logger.log(Level.INFO, "Return editbook.(editFlag =" + editFlag + ")");
			return "editbook";
		}	
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Internal Server Error");
			throw e;
		} 
	}
	
	// Book新規登録
	@PostMapping("/book/insert")
	public String insertBook(@RequestParam(value="user", required=false) String user, @RequestParam String title, @RequestParam String overview, RedirectAttributes redirectAttributes, Model model) {
		try {
			logger.log(Level.INFO, "POST /book/insert");
			logger.log(Level.INFO, " user: " + user);
			logger.log(Level.INFO, " title: " + title);
			logger.log(Level.INFO, " overview: " + overview);
			
			model.addAttribute("user", user); // userをModelに格納
			
			logger.log(Level.INFO, "Create PostBook instance.");
			PostBook postBook = new PostBook();
			postBook.setUser(user);
			postBook.setTitle(title);
			postBook.setOverview(overview);
			
			logger.log(Level.INFO, "Insert book.");
			ResponseEntity<Book> responseBook = postBookInsertApi(postBook); // Book新規登録API実行メソッド
			logger.log(Level.INFO, "Book has returned.(bookid = " + responseBook.getBody().getId() + ")");
			model.addAttribute("book", responseBook.getBody()); // bookをModelに格納
			
			redirectAttributes.addFlashAttribute("complete", "本の登録が完了しました。"); // リダイレクト時のパラメータを設定する（登録成功メッセージ）
			logger.log(Level.INFO, "Redirect to /book/" + responseBook.getBody().getId() + "?user=" + user);
			return "redirect:/book/" + responseBook.getBody().getId() + "?user=" + user; // 登録したBookの詳細ページを返す

		}	
		catch (Exception e) { // Book新規登録API実行メソッドでエラーが発生した場合はhome画面にリダイレクト
			logger.log(Level.SEVERE, "Internal Server Error");
			
			// homeにリダイレクト
			redirectAttributes.addFlashAttribute("complete", "本の登録に失敗しました。"); // リダイレクト時のパラメータを設定する（登録失敗メッセージ）
			logger.log(Level.INFO, "Redirect to /?user=" + user);
			return "redirect:/?user=" + user;
		} 
	}
	
	// Book更新
	@PostMapping("/book/{bookid}/update")
	public String updateBook(@RequestParam(value="user", required=false) String user, @RequestParam String title, @RequestParam String overview, @PathVariable(value="bookid", required=true) int bookid, RedirectAttributes redirectAttributes, Model model) {
		try {
			logger.log(Level.INFO, "POST /book/" + bookid + "/update");
			logger.log(Level.INFO, " user: " + user);
			logger.log(Level.INFO, " title: " + title);
			logger.log(Level.INFO, " overview: " + overview);
			
			model.addAttribute("user", user); // userをModelに格納
			
			// PostBookインスタンスを作成する
			logger.log(Level.INFO, "Create PostBook instance.");
			PostBook postBook = new PostBook();
			postBook.setUser(user);
			postBook.setTitle(title);
			postBook.setOverview(overview);
			
			logger.log(Level.INFO, "Update book.(bookid = " + bookid + ")");
			ResponseEntity<Book> responseBook = postBookUpdateApi(bookid, postBook); // Book更新API実行メソッド
			logger.log(Level.INFO, "Book has returned.(bookid = " + responseBook.getBody().getId() + ")");
			model.addAttribute("book", responseBook.getBody()); // bookをModelに格納
			
			redirectAttributes.addFlashAttribute("complete", "本の更新が完了しました。"); // リダイレクト時のパラメータを設定する（更新成功メッセージ）
			logger.log(Level.INFO, "Redirect to /book/" + responseBook.getBody().getId() + "/detail/?user=" + user);
			return "redirect:/book/" + responseBook.getBody().getId() + "?user=" + user; // 登録したBookの詳細ページを返す

		}
		catch (Exception e) { // Book更新API実行メソッドでエラーが発生した場合はdetail画面にリダイレクト
			logger.log(Level.SEVERE, "Internal Server Error");
			
			redirectAttributes.addFlashAttribute("complete", "本の更新に失敗しました。"); // リダイレクト時のパラメータを設定する（更新失敗メッセージ）
			logger.log(Level.INFO, "Redirect to /book/" + bookid + "?user=" + user);
			return "redirect:/book/" + bookid + "?user=" + user;
		} 
	}
	
	// Book削除
	@DeleteMapping("/book/{bookid}")
	public String deleteBook(@RequestParam(value="user", required=false) String user, @PathVariable int bookid, RedirectAttributes redirectAttributes, Model model) {
		try {
			logger.log(Level.INFO, "DELETE /book/" + bookid + "?user=" + user);
			
			model.addAttribute("user", user); // userをModelに格納
			
			logger.log(Level.INFO, "Delete book.(bookid = " + bookid + ")");
			deleteBookApi(user, bookid); // Book削除API実行メソッド
			
			redirectAttributes.addFlashAttribute("complete", "対象の本の削除が完了しました。"); // リダイレクト時のパラメータを設定する（削除完了メッセージ）
			logger.log(Level.INFO, "redirect:/?user=" + user);
			return "redirect:/" + "?user=" + user;

		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Internal Server Error");
			
			// Book削除APIでエラーが失敗した場合はhomeにリダイレクト
			redirectAttributes.addFlashAttribute("complete", "本の削除に失敗しました。"); // リダイレクト時のパラメータを設定する（削除失敗メッセージ）
			logger.log(Level.INFO, "Redirect to /?user=" + user);
			return "redirect:/?user=" + user;
		} 
	}

	// Review新規登録画面
	@GetMapping("/book/{bookid}/newreview")
	public String newReview(@RequestParam(value="user", required=false) String user, @PathVariable int bookid, RedirectAttributes redirectAttributes, Model model) {
		try {
			logger.log(Level.INFO, "GET /book/" + bookid + "/newreview?user=" + user);
			
			model.addAttribute("user", user); // userをModelに格納
			model.addAttribute("bookid", bookid); // BookのidをModelに格納
			
			logger.log(Level.INFO, "Get Book.(bookid = " + bookid + ")");
			ResponseEntity<Book> responseBook = getBookApi(user, bookid);// Book取得API実行メソッド
			logger.log(Level.INFO, "Book has returned.(bookid = " + bookid + ")");
			model.addAttribute("book", responseBook.getBody()); // bookをModelに格納	
		
			logger.log(Level.INFO, "Return newreview.");
			return "newreview";
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Internal Server Error");
			// Book取得APIでエラーが失敗した場合はhomeにリダイレクト
			redirectAttributes.addFlashAttribute("complete", "対象の本の情報が取得できませんでした。"); // リダイレクト時のパラメータを設定する（Book取得失敗メッセージ）
			logger.log(Level.INFO, "Redirect to /?user=" + user);
			return "redirect:/?user=" + user;
		} 
	}
	
	// Review新規登録
	@PostMapping("/review/insert")
	public String insertReview(@RequestParam(value="user", required=false) String user, @RequestParam int evaluation, @RequestParam String content, @RequestParam int bookid, @RequestParam(defaultValue = "0") int userid, RedirectAttributes redirectAttributes,  Model model) {
		try {
			logger.log(Level.INFO, "POST /review/insert");
			logger.log(Level.INFO, " user: " + user);
			logger.log(Level.INFO, " evaluation: " + evaluation);
			logger.log(Level.INFO, " content: " + content);
			logger.log(Level.INFO, " bookid: " + bookid);
			logger.log(Level.INFO, " userid: " + userid);
			
			model.addAttribute("user", user); // userをModelに格納
			
			logger.log(Level.INFO, "Create PostReview instance.");
			PostReview postReview = new PostReview();
			postReview.setUser(user);
			postReview.setEvaluation(evaluation);
			postReview.setContent(content);
			postReview.setBookid(bookid);
			postReview.setUserid(userid);
			
			logger.log(Level.INFO, "Insert Review.(bookid = " + bookid + ")");
			postReviewApi(postReview); // Review新規登録API実行メソッド
			logger.log(Level.INFO, "Review has inserted.(bookid = " + bookid + ")");
			
			redirectAttributes.addFlashAttribute("complete", "レビューの登録が完了しました。"); // リダイレクト時のパラメータを設定する（登録完了メッセージ）
			logger.log(Level.INFO, "Redirect to /book/" + bookid + "?user=" + user);
			return "redirect:/book/" + bookid + "?user=" + user;
			
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Internal Server Error");
			
			redirectAttributes.addFlashAttribute("complete", "レビューの登録に失敗しました。"); // リダイレクト時のパラメータを設定する（登録失敗メッセージ）
			logger.log(Level.INFO, "Redirect to /book/" + bookid + "?user=" + user);
			return "redirect:/book/" + bookid + "?user=" + user;
		} 
	}
	
	// Review削除
	@DeleteMapping("/book/{bookid}/review/{reviewid}")
	public String deleteReview(@RequestParam(value="user", required=false) String user, @PathVariable int bookid, @PathVariable int reviewid, RedirectAttributes redirectAttributes, Model model) {
		try {
			logger.log(Level.INFO, "DELETE /book/" + bookid + "/review/" + reviewid + "?user=" + user);
			
			model.addAttribute("user", user); // userをModelに格納
			
			logger.log(Level.INFO, "Delete Review.(reviewid =" + reviewid + ")");
			deleteReviewApi(user, reviewid); // Review削除API実行メソッド
			logger.log(Level.INFO, "Review has deleted.(reviewid =" + reviewid + ")");
			
			redirectAttributes.addFlashAttribute("complete", "対象レビューの削除が完了しました。"); // リダイレクト時のパラメータを設定する（削除完了メッセージ）
			logger.log(Level.INFO, "Redirect to /book/" + bookid + "/detail?user=" + user);
			return "redirect:/book/" + bookid + "?user=" + user;
			
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Internal Server Error");
			redirectAttributes.addFlashAttribute("complete", "レビューの削除に失敗しました。"); // リダイレクト時のパラメータを設定する（削除失敗メッセージ）
			logger.log(Level.INFO, "Redirect to /book/" + bookid + "/detail?user=" + user);
			return "redirect:/book/" + bookid + "?user=" + user;
		} 
	}
	
	// -------------------------------------------
	// Book一覧取得API実行メソッド
	// [Book API] GET /api/book	
	ResponseEntity<BookList> getBookListApi(String user) {
		try {
			bookRequestUrl = BOOK_API_URL + "?user=" + user;
			
			logger.log(Level.INFO, "[Book API] GET " + bookRequestUrl);
			ResponseEntity<BookList> responseBook = restTemplate.exchange(bookRequestUrl, HttpMethod.GET, null, BookList.class);
			logger.log(Level.INFO, "[Book API] BookList has returned from Book API.");
			return responseBook;
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "[Book API] Catch Exception");
			logger.log(Level.SEVERE, "[Book API] Unable access to " + "GET " + bookRequestUrl);
			throw e;
		}
	}
	
	// Book一覧取得API実行メソッド（ページ指定）
	// [Book API] GET /api/book?page={page}
	ResponseEntity<BookList> getBookListPageApi(String user, int page) {
		try {
			bookRequestUrl = BOOK_API_URL + "?user=" + user + "&page=" + page;
			
			logger.log(Level.INFO, "[Book API] GET " + bookRequestUrl);
			ResponseEntity<BookList> responseBook = restTemplate.exchange(bookRequestUrl, HttpMethod.GET, null, BookList.class);
			logger.log(Level.INFO, "[Book API] BookList has returned from Book API.");
			return responseBook;
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "[Book API] Catch Exception");
			logger.log(Level.SEVERE, "[Book API] Unable access to " + "GET " + bookRequestUrl);
			throw e;
		}
	}
	
	// Book検索API実行メソッド
	// [Book API] GET /api/book/search
	ResponseEntity<BookList> searchBookListPageApi(String user, String keyword, int page) {
		try {
			bookRequestUrl = BOOK_API_URL + "/search?user=" + user + "&keyword=" + keyword + "&page=" + page;
			
			logger.log(Level.INFO, "[Book API] GET " + bookRequestUrl);
			ResponseEntity<BookList> responseBook = restTemplate.exchange(bookRequestUrl, HttpMethod.GET, null, BookList.class);
			logger.log(Level.INFO, "[Book API] BookList has returned from Book API.");
			return responseBook;
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "[Book API] Catch Exception");
			logger.log(Level.SEVERE, "[Book API] Unable access to " + "GET " + bookRequestUrl);
			throw e;
		}
	}
	
	// Book取得API実行メソッド
	// [Book API] GET /api/book/{bookid}
	ResponseEntity<Book> getBookApi(String user, int bookid) {
		try {
			bookRequestUrl = BOOK_API_URL + "/" + bookid + "?user=" + user;
			
			logger.log(Level.INFO, "[Book API] GET " + bookRequestUrl);
			ResponseEntity<Book> responseBook = restTemplate.exchange(bookRequestUrl, HttpMethod.GET, null, Book.class);
			logger.log(Level.INFO, "[Book API] Book has returned from Book API.");
			
			return responseBook;
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "[Book API] Catch Exception");
			logger.log(Level.SEVERE, "[Book API] Unable access to " + "GET " + bookRequestUrl);
			throw e;
		}
	}
	
	// Book新規登録API実行メソッド
	// [Book API] POST /api/book/insert
	ResponseEntity<Book> postBookInsertApi(PostBook postBook){
		try {
			bookRequestUrl = BOOK_API_URL + "/insert";
			
			HttpEntity<PostBook> entity = new HttpEntity<>(postBook, null);
		
			logger.log(Level.INFO, "[Book API] POST " + bookRequestUrl);
			ResponseEntity<Book> responseBook = restTemplate.exchange(bookRequestUrl, HttpMethod.POST, entity, Book.class);
			logger.log(Level.INFO, "[Book API] Book has returned from Book API.");
			
			return responseBook;
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
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
	ResponseEntity<Book> postBookUpdateApi(int bookid, PostBook postBook) {
		try {
			bookRequestUrl = BOOK_API_URL + "/" + bookid + "/update";
			
			HttpEntity<PostBook> entity = new HttpEntity<>(postBook, null);
			
			logger.log(Level.INFO, "[Book API] POST " + bookRequestUrl);
			ResponseEntity<Book> responseBook = restTemplate.exchange(bookRequestUrl, HttpMethod.POST, entity, Book.class);
			logger.log(Level.INFO, "[Book API] Book has returned from Book API.");
			
			return responseBook;
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "[Book API] Catch Exception");
			logger.log(Level.SEVERE, "[Book API] Unable access to " + "POST " + bookRequestUrl);
			throw e;
		}
	}
	
	// Book削除API
	// [Book API] DELETE /api/book/{bookid}
	void deleteBookApi(String user, int bookid) {
		try {
			bookRequestUrl = BOOK_API_URL + "/" + bookid + "?user=" + user;
			
			logger.log(Level.INFO, "[Book API] Request to Book API.");
			logger.log(Level.INFO, "[Book API] DELETE " + bookRequestUrl);
			ResponseEntity<Void> responseBook = restTemplate.exchange(bookRequestUrl, HttpMethod.DELETE, null, Void.class);
			logger.log(Level.INFO, "[Book API] Book has deleted.");
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "[Book API] Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "[Book API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "[Book API] Catch Exception");
			logger.log(Level.SEVERE, "[Book API] Unable access to " + "DELETE " + bookRequestUrl);
			throw e;
		}
	}
	

	// Review一覧取得API実行メソッド
	// [Review API] GET /api/review
	ResponseEntity<ReviewList> getReviewList(String user, int bookid) {
		try {
			reviewRequestUrl = REVIEW_API_URL + "?user=" + user + "&bookid=" + bookid;
			
			logger.log(Level.INFO, "[Review API] GET " + reviewRequestUrl);
			ResponseEntity<ReviewList> responseReview = restTemplate.exchange(reviewRequestUrl, HttpMethod.GET, null, ReviewList.class);
			logger.log(Level.INFO, "[Review API] ReviewList has returned from Review API.");
			
			return responseReview;
		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Unable access to " + "GET " + reviewRequestUrl);
			throw e;
		}
	}
	
	// Review新規登録API
	// [Review API] POST /api/review/insert
	void postReviewApi(PostReview postReview) {
		try {
			reviewRequestUrl = REVIEW_API_URL + "/insert";
			
			HttpEntity<PostReview> entity = new HttpEntity<>(postReview, null);
			
			logger.log(Level.INFO, "[Review API] POST " + reviewRequestUrl);
			ResponseEntity<Review> responseReview = restTemplate.exchange(reviewRequestUrl, HttpMethod.POST, entity, Review.class);
			logger.log(Level.INFO, "[Review API] Review has returned from Review API.");

		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "[Review API] Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "[Review API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "[Review API] Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "[Review API] Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "[Review API] Catch Exception");
			logger.log(Level.SEVERE, "[Review API] Unable access to " + "POST " + reviewRequestUrl);
			throw e;
		}
	}

	// Review削除API
	// [Review API] DELETE /api/review/{reviewid}
	void deleteReviewApi(String user, int reviewid) {
		try {
			reviewRequestUrl = REVIEW_API_URL + "/" + reviewid + "?user=" + user;
			
			logger.log(Level.INFO, "[Review API] DELETE " + reviewRequestUrl);
			ResponseEntity<Void> reviewResponseBook = restTemplate.exchange(reviewRequestUrl, HttpMethod.DELETE, null, Void.class);
			logger.log(Level.INFO, "[Review API] Review has deleted.(reviewid = "+ reviewid + ")");

		}
		catch (HttpClientErrorException e) {
			logger.log(Level.SEVERE, "Catch  HttpClientErrorException");
			logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (HttpServerErrorException e) {
			logger.log(Level.SEVERE, "Catch HttpServerErrorException");
			logger.log(Level.SEVERE, "Status: " + e.getRawStatusCode() + " Body: " + e.getResponseBodyAsString());
			throw e;
		}
		catch (Exception e) {
			logger.log(Level.SEVERE, "Catch Exception");
			logger.log(Level.SEVERE, "Unable access to " + "DELETE " + reviewRequestUrl);
			throw e;
		}
	}
	
	
	// TotalEvaluation取得API実行メソッド
	// [Review API] GET /api/review/totalevaluation
//	ResponseEntity<TotalEvaluation> getReviewTotalEvaluationApi(String user, int bookid){
//		String reviewRequestUrl = REVIEW_API_URL + "/totalevaluation?user=" + user + "&bookid=" + bookid;
//		
//		try {
//			// TotalEvaluation取得API実行
//			logger.log(Level.INFO, "[Review API] Request to Review API.");
//			logger.log(Level.INFO, "[Review API] GET " + reviewRequestUrl);
//			ResponseEntity<TotalEvaluation> responseBook = restTemplate.exchange(reviewRequestUrl, HttpMethod.GET, null, TotalEvaluation.class);
//			logger.log(Level.INFO, "[Review API] TotalEvaluation has returned from Review API.");
//			
//			return responseBook;
//		}
//		catch (HttpClientErrorException e) {
//			logger.log(Level.SEVERE, "[Review API] Catch  HttpClientErrorException");
//			throw e;
//		}
//		catch (HttpServerErrorException e) {
//			logger.log(Level.SEVERE, "[Review API] Catch HttpServerErrorException");
//			throw e;
//		}
//		catch (Exception e) {
//			logger.log(Level.SEVERE, "[Review API] Catch Exception");
//			logger.log(Level.SEVERE, "[Review API] Unable access to " + "POST " + reviewRequestUrl);
//			throw e;
//		}	
//	}
	
}
