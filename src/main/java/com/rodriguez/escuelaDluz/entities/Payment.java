package com.rodriguez.escuelaDluz.entities;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "payments")
public class Payment implements Serializable{

	private static final long serialVersionUID = -5599665441583115946L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	
	@NotNull(message = "El pago debe tener fecha.")
	private Date paymentDate;
	
	@NotEmpty(message = "Debe tener tipo de pago.")
	private String paymentType;
	
	private Long paymentNumber;

	private Double paymentQuantity;
	
	private String paymentFile;
	
	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student student;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public Long getPaymentNumber() {
		return paymentNumber;
	}

	public void setPaymentNumber(Long paymentNumber) {
		this.paymentNumber = paymentNumber;
	}

	public Double getPaymentQuantity() {
		return paymentQuantity;
	}

	public void setPaymentQuantity(Double paymentQuantity) {
		this.paymentQuantity = paymentQuantity;
	}

	public String getPaymentFile() {
		return paymentFile;
	}

	public void setPaymentFile(String paymentFile) {
		this.paymentFile = paymentFile;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
	
	
	
}
