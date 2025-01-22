package com.rodriguez.escuelaDluz.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rodriguez.escuelaDluz.entities.Appointment;

public interface IAppointmentRepository extends JpaRepository<Appointment, Long>{

}
