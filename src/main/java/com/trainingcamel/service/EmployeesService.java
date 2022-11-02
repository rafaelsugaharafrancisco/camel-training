package com.trainingcamel.service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

		return toEmployeeList(repository.findAll());
	}

	public EmployeeDTO getEmployee(String code) {
		Employee e = repository.findByCode(code).get();
		
		return toEmployeeDto(e);
	}

	public EmployeeDTO addEmployee(EmployeeDTO body) {
		LocalDateTime currentDateTime = LocalDateTime.now();
		
		Random random = new Random();
		int nextInt = random.nextInt(10000001);
		
		DecimalFormat df = new DecimalFormat("00000000");
		body.setCode(df.format(nextInt));
		
		Employee employee = toEmployee(body, currentDateTime);
		
		return toEmployeeDto(repository.save(employee));
		
	}

	@Transactional
	public EmployeeDTO updateEmployee(EmployeeDTO body, String code) {
		Employee e = repository.findByCode(code).get();
		e.setCode(code);
		e.setFullName(body.getFullName());
		e.setCpf(body.getCpf());
		e.setWage(body.getWage());
		e.setUpdatedAt(LocalDateTime.now());
		
		return toEmployeeDto(e);
	}
	
	@Transactional
	public void deleteEmployee(String code) {
		repository.findByCode(code).get();
		
		repository.deleteByCode(code);
		
	}
	
	private EmployeeDTO toEmployeeDto(Employee e) {
		return new EmployeeDTO(e.getCode(), e.getFullName(), e.getCpf(), e.getWage());
	}
	
	private Employee toEmployee(EmployeeDTO dto, LocalDateTime currentDateTime) {
		return new Employee(dto.getCode(), dto.getFullName(), dto.getCpf(), dto.getWage(), currentDateTime, currentDateTime);
	}
	
	private List<EmployeeDTO> toEmployeeList(List<Employee> list) {
		List<EmployeeDTO> employees = new ArrayList<EmployeeDTO>();

		list.forEach(e -> employees.add(toEmployeeDto(e)));

		return employees;
	}
}
