package com.example.demo.service;

import java.util.Optional;

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
	
	// bookidを指定してTotalEvaluationを取得
	public Optional<TotalEvaluation> selectOneById(int bookid) {
		return totalEvaluationRepository.findById(bookid);
	}

	// TotalEvaluationを更新
	public void upsertOne(TotalEvaluation totalEvaluation) {
		totalEvaluationRepository.upsert(totalEvaluation.getBookid(), totalEvaluation.getValue());
	}

	// bookidを指定してTotalEvaluationを削除
	public void deleteOneById(int bookid) {
		totalEvaluationRepository.deleteById(bookid);
	}
}
