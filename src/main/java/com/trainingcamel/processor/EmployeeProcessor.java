package com.trainingcamel.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.support.DefaultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.trainingcamel.dto.Employee;
import com.trainingcamel.service.EmployeesService;

@Component
public class EmployeeProcessor implements Processor {

	@Autowired
	private EmployeesService service;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Employee employee = service.addEmployee(exchange.getIn().getBody(Employee.class));
		
		Message message = new DefaultMessage(exchange.getContext());
		message.setBody(employee);
		message.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.CREATED.value());
		exchange.setMessage(message);
	}

}
