package com.trainingcamel.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trainingcamel.dto.EmployeeDTO;
import com.trainingcamel.model.Employee;
import com.trainingcamel.repository.EmployeeRepository;

@Service
public class EmployeesService {

	@Autowired
	private EmployeeRepository repository;

//	@PostConstruct
//	private void init() {
//		this.employees.add(new EmployeeDTO("0002", "Beltrano", "12345678", 6000.00));
//		this.employees.add(new EmployeeDTO("342435", "Fulano", "00099229", 6200.00));
//		this.employees.add(new EmployeeDTO("987666", "Ciclano", "98978787", 3000.00));
//	}

	public List<EmployeeDTO> getEmployees() {
		List<EmployeeDTO> employees = new ArrayList<EmployeeDTO>();

		repository.findAll()
				.forEach(e -> employees.add(new EmployeeDTO(e.getCode(), e.getFullName(), e.getCpf(), e.getWage())));

		return employees;
	}

	public EmployeeDTO getEmployee(String name) {
		Employee e = repository.findByFullName(name).get();
		
		return new EmployeeDTO(e.getCode(), e.getFullName(), e.getCpf(), e.getWage());
	}

	public EmployeeDTO addEmployee(EmployeeDTO e) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		Employee employee = new Employee(e.getCode(), e.getFullName(), e.getCpf(), e.getWage(), currentDateTime, currentDateTime);
		
		repository.save(employee);
		
		return e;
	}

	@Transactional
	public EmployeeDTO updateEmployee(EmployeeDTO body, String name) {
		Employee e = repository.findByFullName(name).get();
		e.setCode(body.getCode());
		e.setFullName(name);
		e.setCpf(body.getCpf());
		e.setWage(body.getWage());
		e.setUpdatedAt(LocalDateTime.now());
		
		return new EmployeeDTO(e.getCode(), e.getFullName(), e.getCpf(), e.getWage());
	}
	
	@Transactional
	public void deleteEmployee(String name) {
		repository.findByFullName(name).get();
		
		repository.deleteByFullName(name);
		
	}
}
