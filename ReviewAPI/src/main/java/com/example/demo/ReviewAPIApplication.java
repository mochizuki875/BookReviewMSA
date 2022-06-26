package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.entity.TotalEvaluation;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.ReviewService;

@SpringBootApplication
public class ReviewAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewAPIApplication.class, args);
		// デバッグ
	    // SpringApplication.run(ReviewAPIApplication.class, args).getBean(ReviewAPIApplication.class).execute();
	}
	
	// デバッグ
	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	ReviewService reviewService;
	
	private void execute() {	
		// executeGetTotalEvaluation();
		executeGetTopN();
	}
	
	private void executeGetTotalEvaluation() {
		
		int a = 100;
		int b = 200;
		int c = 300;
		int d = 400;
		
		List<Integer> bookids = new ArrayList<>();
		bookids.add(a);
		bookids.add(b);
		bookids.add(c);
		bookids.add(d);
		
		// Iterable<TotalEvaluation> totalEvaluationList = reviewRepository.findTotalEvaluationByBookId(bookids);
		Iterable<TotalEvaluation> totalEvaluationList = reviewService.selectTotalEvaluationByBookId(bookids);
		for(TotalEvaluation totalEvaluation : totalEvaluationList) {
			System.out.println("bookid: " + totalEvaluation.getBookid() + " value: " + totalEvaluation.getValue());
		}	
	}
	
	private void executeGetTopN() {
		Iterable<TotalEvaluation> totalEvaluationList = reviewService.selectTotalEvaluationTopN(30);
		for(TotalEvaluation totalEvaluation : totalEvaluationList) {
			System.out.println("bookid: " + totalEvaluation.getBookid() + " value: " + totalEvaluation.getValue());
		}
	}
}
