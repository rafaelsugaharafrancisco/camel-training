package com.trainingcamel.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity(name = "employees")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NonNull
	@Column(unique = true, nullable = false, length = 8)
	private String code;
	
	@NonNull
	@Column(nullable = false, length = 40, name = "full_name")
	private String fullName;
	
	@NonNull
	@Column(unique = true, nullable = false, length = 11)
	private String cpf;
	
	@NonNull
	@Column(nullable = false)
	private Double wage;
	
	@NonNull
	@Column(nullable = false, name = "inserted_at")
	private LocalDateTime insertedAt;
	
	@NonNull
	@Column(nullable = false, name = "updated_at")
	private LocalDateTime updatedAt;
}
