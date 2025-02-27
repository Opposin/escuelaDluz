package com.rodriguez.escuelaDluz.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "Variables")
public class ManagementVariable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "variableType")
	@NotEmpty(message = "No debe estar vacio.")
	private String variableType;
	
	private String variableValueString;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVariableType() {
		return variableType;
	}

	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}

	public String getVariableValueString() {
		return variableValueString;
	}

	public void setVariableValueString(String variableValueString) {
		this.variableValueString = variableValueString;
	}
	
	
	
}
