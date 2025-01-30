package com.rodriguez.escuelaDluz.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.rodriguez.escuelaDluz.entities.Payment;

public interface IPaymentService{
	
	public void save(Payment payment);
	
	public Payment findById(Long id);
	
	public void delete(Long id);
	
	public List<Payment> findAll();
	
	public List<Payment> findAllSortedByProximity();
	
	
}
