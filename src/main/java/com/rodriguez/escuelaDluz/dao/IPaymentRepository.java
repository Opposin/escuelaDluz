package com.rodriguez.escuelaDluz.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodriguez.escuelaDluz.entities.Payment;
import com.rodriguez.escuelaDluz.entities.Student;

public interface IPaymentRepository extends  JpaRepository<Payment, Long>{

	 void deleteAllByStudent(Student student);
}
