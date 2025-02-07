package com.rodriguez.escuelaDluz.entities;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "students")
public class Student implements Serializable {

	private static final long serialVersionUID = 6025275150458467399L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "firstName")
	@NotEmpty(message = "No debe estar vacio.")
	private String firstName;

	@Column(name = "secondName")
	@NotEmpty(message = "No debe estar vacio.")
	private String lastName;

	@Column(name = "studentAddress")
	@NotEmpty(message = "No debe estar vacio.")
	private String studentAddress;

	@Column(name = "studentPartido")
	@NotEmpty(message = "No debe estar vacio.")
	private String studentPartido;

	private String studentRecomen;

	@Column(length = 500)
	private String studentComment;

	@NotNull(message = "Debe ser una fecha valida.")
	private Date studentCourseBegg;
	
	@NotNull(message = "Debe ser una fecha valida.")
	private Date studentCourseEnd;
	
	private Date studentInactiveDate;
	
	@NotEmpty(message = "No debe estar vacio.")
	private String studentRecepcionist;

	@Column(name = "DNI")
	@NotNull(message = "El DNI no debe estar vacio.")
	private Long studentDNI;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Appointment> studentAppointments;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Payment> studentPayments;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Exam> studentExams;

	
	@NotNull(message = "El numero telefonico debe estar vacio.")
	private Long studentTel;
	
	private Long studentClass;
	
	@NotNull(message = "Se debe definir cuantas clases tendra el alumno")
	private Long studentClasses;

	@Column(name = "studentAge", nullable = false)
//	@NotNull(message = "No debe estar vacio.")
	private Long studentAge;

//	@NotNull(message = "No debe estar vacio.")
	private Long studentNonAtten;
	
	private Boolean studentAlertNonActive;

	private Boolean studentPdf;

	private Boolean studentZigZag;
	
	private Boolean studentMotorTalk;
	
	private Boolean student45degree;
	
	private Boolean studentIncline;
	
	private Boolean studentAdminSellCon;
	
	private Boolean studentInactive;

	private Boolean studentEstacionar;

	private Boolean studentGiroU;

	private Boolean studentVideoPista;

	private Boolean studentGraduate;
	
	
	private Long studentGrade;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getStudentAddress() {
		return studentAddress;
	}

	public void setStudentAddress(String studentAddress) {
		this.studentAddress = studentAddress;
	}

	public String getStudentPartido() {
		return studentPartido;
	}

	public void setStudentPartido(String studentPartido) {
		this.studentPartido = studentPartido;
	}

	public String getStudentRecomen() {
		return studentRecomen;
	}
	
	public String getStudentComment() {
		return studentComment;
	}

	public void setStudentComment(String studentComment) {
		this.studentComment = studentComment;
	}

	public void setStudentRecomen(String studentRecomen) {
		this.studentRecomen = studentRecomen;
	}

	public Date getStudentCourseBegg() {
		return studentCourseBegg;
	}

	public void setStudentCourseBegg(Date studentCourseBegg) {
		this.studentCourseBegg = studentCourseBegg;
	}

	public Long getStudentDNI() {
		return studentDNI;
	}

	public void setStudentDNI(Long studentDNI) {
		this.studentDNI = studentDNI;
	}

	public List<Appointment> getAppointments() {
		return studentAppointments;
	}

	public void setAppointments(List<Appointment> appointments) {
		this.studentAppointments = appointments;
	}

	public List<Payment> getStudentPayments() {
		return studentPayments;
	}

	public void setStudentPayments(List<Payment> studentPayments) {
		this.studentPayments = studentPayments;
	}

	public Long getStudentTel() {
		return studentTel;
	}

	public void setStudentTel(Long studentTel) {
		this.studentTel = studentTel;
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

	public Boolean getStudentPdf() {
		return studentPdf;
	}

	public void setStudentPdf(Boolean studentPdf) {
		this.studentPdf = studentPdf;
	}

	public Boolean getStudentZigZag() {
		return studentZigZag;
	}

	public void setStudentZigZag(Boolean studentZigZag) {
		this.studentZigZag = studentZigZag;
	}

	public Boolean getStudentEstacionar() {
		return studentEstacionar;
	}

	public void setStudentEstacionar(Boolean studentEstacionar) {
		this.studentEstacionar = studentEstacionar;
	}

	public Boolean getStudentGiroU() {
		return studentGiroU;
	}

	public void setStudentGiroU(Boolean studentGiroU) {
		this.studentGiroU = studentGiroU;
	}

	public Boolean getStudentVideoPista() {
		return studentVideoPista;
	}

	public void setStudentVideoPista(Boolean studentVideoPista) {
		this.studentVideoPista = studentVideoPista;
	}

	public Boolean getStudentGraduate() {
		return studentGraduate;
	}

	public void setStudentGraduate(Boolean studentGraduate) {
		this.studentGraduate = studentGraduate;
	}

	public List<Appointment> getStudentAppointments() {
		return studentAppointments;
	}

	public void setStudentAppointments(List<Appointment> studentAppointments) {
		this.studentAppointments = studentAppointments;
	}

	public Date getStudentCourseEnd() {
		return studentCourseEnd;
	}

	public void setStudentCourseEnd(Date studentCourseEnd) {
		this.studentCourseEnd = studentCourseEnd;
	}

	public Long getStudentClasses() {
		return studentClasses;
	}

	public void setStudentClasses(Long studentClasses) {
		this.studentClasses = studentClasses;
	}

	public Long getStudentClass() {
		return studentClass;
	}

	public void setStudentClass(Long studentClass) {
		this.studentClass = studentClass;
	}

	public Boolean getStudentMotorTalk() {
		return studentMotorTalk;
	}

	public void setStudentMotorTalk(Boolean studentMotorTalk) {
		this.studentMotorTalk = studentMotorTalk;
	}

	public Boolean getStudentAdminSellCon() {
		return studentAdminSellCon;
	}

	public void setStudentAdminSellCon(Boolean studentAdminSellCon) {
		this.studentAdminSellCon = studentAdminSellCon;
	}

	public Boolean getStudentInactive() {
		return studentInactive;
	}

	public void setStudentInactive(Boolean studentActive) {
		this.studentInactive = studentActive;
	}

	public Date getStudentGraduateDate() {
		return studentInactiveDate;
	}

	public void setStudentGraduateDate(Date studentGraduateDate) {
		this.studentInactiveDate = studentGraduateDate;
	}

	public Boolean getStudent45degree() {
		return student45degree;
	}

	public void setStudent45degree(Boolean student45degree) {
		this.student45degree = student45degree;
	}

	public Boolean getStudentIncline() {
		return studentIncline;
	}

	public void setStudentIncline(Boolean studentIncline) {
		this.studentIncline = studentIncline;
	}

	public Long getStudentGrade() {
		return studentGrade;
	}

	public void setStudentGrade(Long studentGrade) {
		this.studentGrade = studentGrade;
	}

	public Date getStudentInactiveDate() {
		return studentInactiveDate;
	}

	public void setStudentInactiveDate(Date studentInactiveDate) {
		this.studentInactiveDate = studentInactiveDate;
	}

	public String getStudentRecepcionist() {
		return studentRecepcionist;
	}

	public void setStudentRecepcionist(String studentRecepcionist) {
		this.studentRecepcionist = studentRecepcionist;
	}

	public Boolean getStudentAlertNonActive() {
		return studentAlertNonActive;
	}

	public void setStudentAlertNonActive(Boolean studentAlertNonActive) {
		this.studentAlertNonActive = studentAlertNonActive;
	}

	public List<Exam> getStudentExams() {
		return studentExams;
	}

	public void setStudentExams(List<Exam> studentExams) {
		this.studentExams = studentExams;
	}
	
	
	
}
	


