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
	private Boolean studentGraduate;

	private Long appointmentId;
	private Date appointmentDate;
	private String appointmentTime;
	private String appointmentComplete;
	private Long appointmentClassNumber;

	private Long paymentId;
	private Date paymentDate;
	private String paymentTime;
	private Long paymentNumber;

	// Constructor
	public StudentAppointmentDTO(Student student, Appointment appointment, Payment payment) {
		this.studentId = student.getId();
		this.studentName = student.getFistName();
		this.studentLastName = student.getLastName();
		this.studentPartido = student.getStudentPartido();
		this.studentAddress = student.getStudentAddress();
		this.StudentDNI = student.getStudentDNI();
		this.studentAge = student.getStudentAge();
		this.studentNonAtten = student.getStudentNonAtten();
		this.studentGraduate = student.getStudentGraduate();
		if (appointment != null) {
			this.appointmentDate = appointment.getAppointmentDate();
			this.appointmentTime = appointment.getAppointmentTime();
			this.appointmentId = appointment.getId();
			this.appointmentId = appointment.getAppointmentClassNumber();
			if (!appointment.getAppointmentComplete().equals("Turno Pendiente.")) {
				this.appointmentComplete = appointment.getAppointmentComplete();
			} else {
				this.appointmentComplete = appointment.getAppointmentComplete();
			}

		} else {
			this.appointmentId = null;
			this.appointmentDate = null;
			this.appointmentTime = ""; // Mensaje para cuando no hay turno
			this.appointmentClassNumber = null;
			this.appointmentComplete = null;
		}
		if (payment != null) {
			this.paymentId = payment.getId();
			this.paymentDate = payment.getPaymentDate();
			this.paymentTime = payment.getPaymentTime();
			this.paymentNumber = payment.getPaymentNumber();
		} else {
			this.paymentId = null;
			this.paymentDate = null;
			this.paymentTime = "";
			this.paymentNumber = null;
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

	public Long getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(Long appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getStudentPartido() {
		return studentPartido;
	}

	public void setStudentPartido(String studentPartido) {
		this.studentPartido = studentPartido;
	}

	public Boolean getStudentGraduate() {
		return studentGraduate;
	}

	public void setStudentGraduate(Boolean studentGraduate) {
		this.studentGraduate = studentGraduate;
	}

	public Long getAppointmentClassNumber() {
		return appointmentClassNumber;
	}

	public void setAppointmentClassNumber(Long appointmentClassNumber) {
		this.appointmentClassNumber = appointmentClassNumber;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentTime() {
		return paymentTime;
	}

	public void setPaymentTime(String paymentTime) {
		this.paymentTime = paymentTime;
	}

	public Long getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(Long paymentNumber) {
		this.paymentNumber = paymentNumber;
	}

}
