package com.rodriguez.escuelaDluz.entities;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "appointments")
public class Appointment implements Serializable{


	private static final long serialVersionUID = 5887162995685314463L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "fecha")
	@Temporal(TemporalType.DATE)
	private Date appointmentDate;
	
	
	private String appointmentTime;
	
	private String appointmentTime2;
	
	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student student;
	
	@ManyToOne
	@JoinColumn(name = "employee_id")
	private Employee employee;
	
	private String appointmentComplete;
	
	private String appointmentType;
	
	private Long appointmentClassNumber;
	
	private Long appointmentClassNumber2;
	
	private Boolean appointmentInactiveInstructorAlert;
	
	@Version
    private Long version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(Date appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public String getAppointmentTime() {
		return appointmentTime;
	}

	public void setAppointmentTime(String appointmentTime) {
		this.appointmentTime = appointmentTime;
	}

	public String getAppointmentComplete() {
		return appointmentComplete;
	}

	public void setAppointmentComplete(String appointmentComplete) {
		this.appointmentComplete = appointmentComplete;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public String getAppointmentType() {
		return appointmentType;
	}

	public void setAppointmentType(String appointmentType) {
		this.appointmentType = appointmentType;
	}

	public Long getAppointmentClassNumber() {
		return appointmentClassNumber;
	}

	public void setAppointmentClassNumber(Long appointmentClassNumber) {
		this.appointmentClassNumber = appointmentClassNumber;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public String getAppointmentTime2() {
		return appointmentTime2;
	}

	public void setAppointmentTime2(String appointmentTime2) {
		this.appointmentTime2 = appointmentTime2;
	}

	public Long getAppointmentClassNumber2() {
		return appointmentClassNumber2;
	}

	public void setAppointmentClassNumber2(Long appointmentClassNumber2) {
		this.appointmentClassNumber2 = appointmentClassNumber2;
	}

	public Boolean getAppointmentInactiveInstructorAlert() {
		return appointmentInactiveInstructorAlert;
	}

	public void setAppointmentInactiveInstructorAlert(Boolean appointmentInactiveInstructorAlert) {
		this.appointmentInactiveInstructorAlert = appointmentInactiveInstructorAlert;
	}
	
}
