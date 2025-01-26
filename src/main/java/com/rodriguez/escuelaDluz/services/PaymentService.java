package com.rodriguez.escuelaDluz.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodriguez.escuelaDluz.dao.IAppointmentRepository;
import com.rodriguez.escuelaDluz.dao.IPaymentRepository;
import com.rodriguez.escuelaDluz.dao.IStudentRepository;
import com.rodriguez.escuelaDluz.entities.Payment;

import jakarta.transaction.Transactional;


@Service
public class PaymentService implements IPaymentService {
	
	@Autowired
	IAppointmentRepository appointmentRepository;

	@Autowired
	IStudentRepository studentRepository;
	
	@Autowired
	IPaymentRepository paymentRepository;
	
	@Override
	@Transactional
	public void save(Payment payment) {
		paymentRepository.save(payment);

	}

	@Override
	public Payment findById(Long id) {
		return paymentRepository.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		paymentRepository.deleteById(id);

	}

	@Override
	public List<Payment> findAll() {
		return paymentRepository.findAll();
	}

}
