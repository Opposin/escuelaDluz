package com.rodriguez.escuelaDluz.entities;

import java.sql.Date;

public class StudentAppointmentDTO {
	private Long studentId;
	private String studentName;
	private String studentLastName;
	private String studentAddress;
	private String studentPartido;
	private Long StudentDNI;
	private Long studentAge;
	private Long studentNonAtten;
//	private Appointment nextAppointment;
	private Date appointmentDate;
	private String appointmentTime;

	// Constructor
	public StudentAppointmentDTO(Student student, Appointment appointment) {
	        this.studentId = student.getId();
	        this.studentName = student.getFistName();
	        this.studentLastName = student.getLastName();
	        this.studentPartido = student.getStudentPartido();
	        this.studentAddress = student.getStudentAddress();
	        this.StudentDNI = student.getStudentDNI();
	        this.studentAge = student.getStudentAge();
	        this.studentNonAtten = student.getStudentNonAtten();
	        if (appointment != null) {
	            this.appointmentDate = appointment.getAppointmentDate();
	            this.appointmentTime = appointment.getAppointmentTime();
	        } else {
	            this.appointmentDate = null;
	            this.appointmentTime = "";  // Mensaje para cuando no hay turno
	        }
	    }

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getStudentLastName() {
		return studentLastName;
	}

	public void setStudentLastName(String studentLastName) {
		this.studentLastName = studentLastName;
	}

	public String getStudentAddress() {
		return studentAddress;
	}

	public void setStudentAddress(String studentAddress) {
		this.studentAddress = studentAddress;
	}

	public Long getStudentDNI() {
		return StudentDNI;
	}

	public void setStudentDNI(Long studentDNI) {
		StudentDNI = studentDNI;
	}

	public Long getStudentAge() {
		return studentAge;
	}

	public void setStudentAge(Long studentAge) {
		this.studentAge = studentAge;
	}

	public Long getStudentNonAtten() {
		return studentNonAtten;
	}

	public void setStudentNonAtten(Long studentNonAtten) {
		this.studentNonAtten = studentNonAtten;
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

	
	
	public String getStudentPartido() {
		return studentPartido;
	}

//	public Appointment getNextAppointment() {
//		return nextAppointment;
//	}
//
//	public void setNextAppointment(Appointment nextAppointment) {
//		this.nextAppointment = nextAppointment;
//	}

	public void setStudentPartido(String studentPartido) {
		this.studentPartido = studentPartido;
	}

	
	
}
