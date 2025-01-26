package com.rodriguez.escuelaDluz.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodriguez.escuelaDluz.entities.Payment;

public interface IPaymentRepository extends  JpaRepository<Payment, Long>{

}
