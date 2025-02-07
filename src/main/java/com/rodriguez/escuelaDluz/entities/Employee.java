package com.rodriguez.escuelaDluz.entities;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "employes")
public class Employee  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2126933428942392853L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "firstName")
	@NotEmpty(message = "No debe estar vacio.")
	private String employeeFirstName;

	@Column(name = "secondName")
	@NotEmpty(message = "No debe estar vacio.")
	private String employeeSecondName;
	
	@NotNull(message = "El telefono no debe estar vacio.")
	private Long employeeCell;
	
	@Column(name = "DNI", unique = true)
	@NotNull(message = "El DNI no debe estar vacio.")
	private Long employeeDNI;
	
	@NotEmpty(message = "No debe estar vacio.")
	private String employeeAdress;
	
	@NotEmpty(message = "No debe estar vacio.")
	private String employeePartido;
	
	@NotEmpty(message = "No debe estar vacio.")
	private String employeeType;
	
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Appointment> employeeAppointments;
	
	private Boolean employeeActive;
	
	private String employeeComment;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmployeeType() {
		return employeeType;
	}

	public void setEmployeeType(String employeeType) {
		this.employeeType = employeeType;
	}

	public Boolean getEmployeeActive() {
		return employeeActive;
	}

	public void setEmployeeActive(Boolean employeActive) {
		this.employeeActive = employeActive;
	}

	public Long getEmployeeCell() {
		return employeeCell;
	}

	public void setEmployeeCell(Long employeeCell) {
		this.employeeCell = employeeCell;
	}

	public Long getEmployeeDNI() {
		return employeeDNI;
	}

	public void setEmployeeDNI(Long employeeDNI) {
		this.employeeDNI = employeeDNI;
	}

	public String getEmployeeAdress() {
		return employeeAdress;
	}

	public void setEmployeeAdress(String employeeAdress) {
		this.employeeAdress = employeeAdress;
	}

	public String getEmployeePartido() {
		return employeePartido;
	}

	public void setEmployeePartido(String employeePartido) {
		this.employeePartido = employeePartido;
	}

	public String getEmployeeFirstName() {
		return employeeFirstName;
	}

	public void setEmployeeFirstName(String employeeFirstName) {
		this.employeeFirstName = employeeFirstName;
	}

	public String getEmployeeSecondName() {
		return employeeSecondName;
	}

	public void setEmployeeSecondName(String employeeSecondName) {
		this.employeeSecondName = employeeSecondName;
	}

	public String getEmployeeComment() {
		return employeeComment;
	}

	public void setEmployeeComment(String employeeComment) {
		this.employeeComment = employeeComment;
	}

	public List<Appointment> getEmployeeAppointments() {
		return employeeAppointments;
	}

	public void setEmployeeAppointments(List<Appointment> employeeAppointments) {
		this.employeeAppointments = employeeAppointments;
	}
	
	
}
