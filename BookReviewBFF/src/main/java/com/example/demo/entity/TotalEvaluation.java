package com.example.demo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

// totalevaluationテーブル用のエンティティ
@Table("totalevaluation")
public class TotalEvaluation {
	@Id
	@Column("bookid")
	private int bookid; // 主キー
	@Column("value")
	private double value = 0; // totalevaluation（1~5）
	
	public TotalEvaluation() {
	}
	
	public TotalEvaluation(int bookid, double value) {
		this.bookid = bookid;
		this.value = value;
	}
	
	public int getBookid() {
		return bookid;
	}
	
	public void setBookid(int bookid) {
		this.bookid = bookid;
	}
	
	public double getValue() {
		return value;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
}
