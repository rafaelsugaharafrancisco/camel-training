package com.trainingcamel.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO implements Serializable {
	
	private String code;
	private String fullName;
	private String cpf;
	private double wage;

}
