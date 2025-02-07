package com.rodriguez.escuelaDluz.services;

import java.util.List;

import com.rodriguez.escuelaDluz.entities.Exam;

public interface IExamService {

	public void save(Exam exam);
	
	public Exam findById(Long id);
	
	public void delete(Long id);
	
	public List<Exam> findAll();
	
	public List<Exam> findAlerts();
}
