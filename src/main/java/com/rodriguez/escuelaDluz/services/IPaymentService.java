package com.rodriguez.escuelaDluz.services;

import java.util.List;

import com.rodriguez.escuelaDluz.entities.Payment;

public interface IPaymentService{
	
	public void save(Payment payment);
	
	public Payment findById(Long id);
	
	public void delete(Long id);
	
	public List<Payment> findAll();
	
}
