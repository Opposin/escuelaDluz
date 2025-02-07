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
import jakarta.persistence.Version;

@Entity
@Table(name = "exams")
public class Exam  implements  Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2597650424640439538L;
		
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student student;
	
	private Boolean examAproved;
	
	private String examType;
	
	private String examTry;
	
	private Date examDate;
	
	private Boolean examAlert;
	
	@Version
    private Long version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Boolean getExamAproved() {
		return examAproved;
	}

	public void setExamAproved(Boolean examAproved) {
		this.examAproved = examAproved;
	}

	public String getExamType() {
		return examType;
	}

	public void setExamType(String examType) {
		this.examType = examType;
	}

	public Date getExamDate() {
		return examDate;
	}

	public void setExamDate(Date examDate) {
		this.examDate = examDate;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getExamTry() {
		return examTry;
	}

	public void setExamTry(String examTry) {
		this.examTry = examTry;
	}

	public Boolean getExamAlert() {
		return examAlert;
	}

	public void setExamAlert(Boolean examAlert) {
		this.examAlert = examAlert;
	}
	
}
