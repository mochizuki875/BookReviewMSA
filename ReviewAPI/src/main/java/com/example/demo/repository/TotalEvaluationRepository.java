package com.example.demo.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.TotalEvaluation;

//CrudRepositoryの拡張インターフェースとしてRepositoryを作成
public interface TotalEvaluationRepository extends CrudRepository<TotalEvaluation, Integer> {

	// bookidに対応するvalueが登録されていなければINSERT、登録されていればUPDATE
	@Modifying
	@Query("INSERT INTO totalevaluation(bookid, value) values(:bookid, :value) ON CONFLICT ON CONSTRAINT totalevaluation_bookid_key DO UPDATE SET value = :value;")
	void upsert(@Param("bookid") int bookid, @Param("value") double value);

}
