package com.example.demo.service;

import java.util.Optional;

import com.example.demo.entity.TotalEvaluation;

public interface TotalEvaluationService {
	
	// bookidを指定してTotalEvaluationを取得
	Optional<TotalEvaluation> selectOneById(int bookid);
	
	// TotalEvaluationを更新
	void upsertOne(TotalEvaluation totalEvaluation);

	// bookidを指定してTotalEvaluationを削除
	void deleteOneById(int bookid);

}
