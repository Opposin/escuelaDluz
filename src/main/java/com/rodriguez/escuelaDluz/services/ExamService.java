package com.rodriguez.escuelaDluz.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodriguez.escuelaDluz.dao.IExamRepository;
import com.rodriguez.escuelaDluz.entities.Exam;

import jakarta.transaction.Transactional;

@Service
public class ExamService implements IExamService {

	@Autowired
	IExamRepository examRepository;
	
	@Override
	@Transactional
	public void save(Exam exam) {
		examRepository.save(exam);
	}

	@Override
	public Exam findById(Long id) {
		return examRepository.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		examRepository.deleteById(id);
	}

	@Override
	public List<Exam> findAll() {
		return examRepository.findAll();
	}

	@Override
	public List <Exam> findAlerts(){
		List<Exam> exams = examRepository.findByExamAlertTrue();

        // Filtrar los exámenes que cumplen con las condiciones
        return exams.stream()
                .filter(exam -> {
                    LocalDate examDate = exam.getExamDate().toLocalDate();
                    LocalDate currentDate = LocalDate.now();
                    long daysPassed = java.time.temporal.ChronoUnit.DAYS.between(examDate, currentDate);

                    if (exam.getExamType().equals("Teorico") && exam.getExamTry().equals("primer intento")) {
                        if (!exam.getExamAproved() && daysPassed >= 23) return true;
                        if (exam.getExamAproved() && daysPassed >= 23) return true;
                    }

                    if (exam.getExamType().equals("Teorico") && exam.getExamTry().equals("segundo intento") && exam.getExamAproved()) {
                        return true; // Se agrega inmediatamente
                    }

                    if (exam.getExamType().equals("Practico") && exam.getExamTry().equals("primer intento") && !exam.getExamAproved() && daysPassed >= 23) {
                        return true;
                    }

                    return false;
                })
                .collect(Collectors.toList());
	}
}
