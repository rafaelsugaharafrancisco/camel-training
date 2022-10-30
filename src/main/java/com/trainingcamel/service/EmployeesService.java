package com.trainingcamel.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.trainingcamel.dto.EmployeeDTO;

@Service
public class EmployeesService {

	private List<EmployeeDTO> employees = new ArrayList<>();

	@PostConstruct
	private void init() {
		this.employees.add(new EmployeeDTO(1, "Beltrano", "12345678", 6000.00));
		this.employees.add(new EmployeeDTO(2, "Fulano", "00099229", 6200.00));
		this.employees.add(new EmployeeDTO(3, "Ciclano", "98978787", 3000.00));
	}

	public List<EmployeeDTO> getEmployees() {
		return this.employees;
	}

	public EmployeeDTO getEmployee(String employee) {
		return this.employees.stream().filter(e -> e.getNome().equals(employee)).findFirst().get();
	}

	public EmployeeDTO addEmployee(EmployeeDTO employee) {
		this.employees.add(employee);
		return employee;
	}
}
