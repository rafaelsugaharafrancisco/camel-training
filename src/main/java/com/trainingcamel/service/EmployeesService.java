package com.trainingcamel.service;

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
				.forEach(e -> employees.add(new EmployeeDTO(e.getCodigo(), e.getNome(), e.getCpf(), e.getSalario())));

		return employees;
	}

	public EmployeeDTO getEmployee(String name) {
		Employee e = repository.findByNome(name).get();
		
		return new EmployeeDTO(e.getCodigo(), e.getNome(), e.getCpf(), e.getSalario());
	}

	public EmployeeDTO addEmployee(EmployeeDTO e) {
		Employee employee = new Employee(e.getCodigo(), e.getNome(), e.getCpf(), e.getSalario());
		repository.save(employee);
		
		return e;
	}

	@Transactional
	public EmployeeDTO updateEmployee(EmployeeDTO body, String name) {
		Employee e = repository.findByNome(name).get();
		e.setCodigo(body.getCodigo());
		e.setNome(name);
		e.setCpf(body.getCpf());
		e.setSalario(body.getSalario());
		
		return new EmployeeDTO(e.getCodigo(), e.getNome(), e.getCpf(), e.getSalario());
	}
	
	@Transactional
	public void deleteEmployee(String name) {
		repository.findByNome(name).get();
		
		repository.deleteByNome(name);
		
	}
}
