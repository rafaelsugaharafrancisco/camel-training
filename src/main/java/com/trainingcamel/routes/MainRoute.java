package com.trainingcamel.routes;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.support.DefaultMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.trainingcamel.dto.Employee;
import com.trainingcamel.dto.MessageDto;
import com.trainingcamel.processor.EmployeeProcessor;
import com.trainingcamel.service.EmployeesService;

@Component
public class MainRoute extends RouteBuilder {

	@Autowired
	private EmployeesService service;

	@Autowired
	private EmployeeProcessor processor;

	@Override
	public void configure() throws Exception {

//		onException(NoSuchElementException.class)
//	    	.handled(true)
//	    	.transform().simple("${exception.message}");
//			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NOT_FOUND.value()));
//	    	.process(new Processor() {
//
//				@Override
//				public void process(Exchange exchange) throws Exception {
//					String name = exchange.getIn().getHeader("employee", String.class);
//					
//					exchange.getIn().removeHeader("*");
//					exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_FOUND.value());
//					exchange.getIn().setBody(new MessageDto("Nome " + name + " não encontrado no BD"));
//				}
//	    		
//	    	});

		restConfiguration().component("servlet").host("localhost").port(8080).bindingMode(RestBindingMode.auto);

		rest("/api/V1/says/hello").get().produces(MediaType.APPLICATION_JSON_VALUE).to("direct:hello");

		rest("/api/V1/employees")
			.produces(MediaType.APPLICATION_JSON_VALUE).consumes(MediaType.APPLICATION_JSON_VALUE)
			.get()
//				.outType(Employee.class)
				.to("direct:get-employees")
			.get("/{employee}").outType(Employee.class)
				.to("direct:get-employee")
			.post().type(Employee.class)
				.to("direct:add-employee").outType(Employee.class);

		from("direct:hello").routeId("welcome").setBody(constant("Hello world"));

		from("direct:get-employees").routeId("employees-list").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				List<Employee> employees = service.getEmployees();

				Message message = new DefaultMessage(exchange.getContext());
				message.setBody(employees);
				exchange.setMessage(message);

			}

		});

		from("direct:get-employee").routeId("employee-one").doTry().process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				Employee employee = service.getEmployee(exchange.getIn().getHeader("employee", String.class));

				exchange.getIn().setBody(employee);

			}

		}).doCatch(NoSuchElementException.class).process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				String name = exchange.getIn().getHeader("employee", String.class);

				exchange.getIn().removeHeaders("*");
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_FOUND.value());
				exchange.getIn().setBody(new MessageDto(name + " não encontrado no BD"));
			}

		});

		from("direct:add-employee").routeId("add-employee").process(processor);
	}

}
