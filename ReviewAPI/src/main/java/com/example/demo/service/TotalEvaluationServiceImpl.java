package com.example.demo.service;

import java.util.Optional;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.TotalEvaluation;
import com.example.demo.repository.TotalEvaluationRepository;

@Service
@Transactional
public class TotalEvaluationServiceImpl implements TotalEvaluationService {
	@Autowired
	TotalEvaluationRepository totalEvaluationRepository;
	
	Logger logger = Logger.getLogger(ReviewServiceImpl.class.getName());
	ConsoleHandler handler = new ConsoleHandler();
	
	// bookidを指定してTotalEvaluationを取得
	public Optional<TotalEvaluation> selectOneById(int bookid) {
		logger.log(Level.FINER, "totalEvaluationRepository.findById(" + bookid + ")");
		return totalEvaluationRepository.findById(bookid);
	}

	// TotalEvaluationを更新
	public void upsertOne(TotalEvaluation totalEvaluation) {
		logger.log(Level.FINER, "totalEvaluationRepository.upsert(" + totalEvaluation.getBookid() + ", " + totalEvaluation.getValue() + ")");
		totalEvaluationRepository.upsert(totalEvaluation.getBookid(), totalEvaluation.getValue());
	}

	// bookidを指定してTotalEvaluationを削除
	public void deleteOneById(int bookid) {
		logger.log(Level.FINER, "totalEvaluationRepository.deleteById(" + bookid + ")");
		totalEvaluationRepository.deleteById(bookid);
	}
}
