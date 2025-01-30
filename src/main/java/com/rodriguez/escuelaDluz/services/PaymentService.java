package com.rodriguez.escuelaDluz.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

	@Override
	public List<Payment> findAllSortedByProximity() {
		List<Payment> payments = paymentRepository.findAll();

		// Ordenar por proximidad a la fecha y hora actual
		LocalDateTime now = LocalDateTime.now();
		return payments.stream().sorted(Comparator.comparing(payment -> calculateProximity(payment, now)))
				.collect(Collectors.toList());
	}

	private Duration calculateProximity(Payment payment, LocalDateTime now) {
		LocalDate paymentDate = payment.getPaymentDate().toLocalDate();
		LocalTime paymentTime = LocalTime.parse(payment.getPaymentTime()); // Asegúrate del formato correcto hh:mm
		LocalDateTime paymentDateTime = LocalDateTime.of(paymentDate, paymentTime);
		return Duration.between(now, paymentDateTime).abs();
	}

}
