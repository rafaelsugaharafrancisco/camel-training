package com.trainingcamel.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO implements Serializable {
	
	private String codigo;
	private String nome;
	private String cpf;
	private double salario;

}
