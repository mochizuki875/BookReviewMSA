package com.example.demo.entity;

public class BookList {
	Iterable<Book> bookListPage;
	int page;
	int allPages;
	
	public BookList() {
	}
	
	public BookList(Iterable<Book> bookListPage, int page, int allPages) {
		this.bookListPage = bookListPage;
		this.page = page;
		this.allPages = allPages;
	}
	
	public void setBookListPage(Iterable<Book> bookListPage) {
		this.bookListPage = bookListPage;
	}
	
	public Iterable<Book> getBookListPage() {
		return bookListPage;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	public int getPage() {
		return page;
	}
	
	public void setAllPages(int allPages) {
		this.allPages = allPages;
	}
	
	public int getAllPages() {
		return allPages;
	}
	
}
