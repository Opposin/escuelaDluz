package com.rodriguez.escuelaDluz.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodriguez.escuelaDluz.dao.IVariableRepository;
import com.rodriguez.escuelaDluz.entities.ManagementVariable;

import jakarta.transaction.Transactional;

@Service
public class VariableService implements IVariableService{

	@Autowired
	IVariableRepository variableRepository;
	
	@Override
	@Transactional
	public void save(ManagementVariable variable) {
		variableRepository.save(variable);
	}

	@Override
	public ManagementVariable findById(Long id) {
		return variableRepository.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		variableRepository.deleteById(id);
		
	}

	@Override
	public List<ManagementVariable> findAll() {
		return variableRepository.findAll();
	}
	
	
	private static final List<String> HORARIOS_BASE = Arrays.asList("08:00", "08:40", "09:20", "10:00", "10:40",
			"11:20", "12:00", "12:40", "13:20", "14:00", "14:40", "15:20", "16:00", "16:40", "17:20", "18:00", "18:40",
			"19:20");

	
	@Override
	public List<String> horariosCompletos() {
		// Obtener todas las variables desde el repositorio
		List<ManagementVariable> variables = variableRepository.findAll();

		List<String> nuevosHorarios = variables.stream().filter(v -> "time".equals(v.getVariableType())) // Solo incluir
																											// si
																											// variableType
																											// es "time"
				.map(ManagementVariable::getVariableValueString).filter(Objects::nonNull) // Evitar nulos
				.distinct() // Eliminar duplicados
				.sorted() // Ordenar los horarios nuevos
				.collect(Collectors.toList());

		// Fusionar ambas listas en orden sin duplicados
		TreeSet<String> horariosActualizados = new TreeSet<>(HORARIOS_BASE);
		horariosActualizados.addAll(nuevosHorarios);

		return new ArrayList<>(horariosActualizados);
	}
	
}
