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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.trainingcamel.dto.EmployeeDTO;
import com.trainingcamel.dto.MessageDto;
import com.trainingcamel.processor.EmployeeProcessor;
import com.trainingcamel.service.EmployeesService;

//@Component
public class MainRoute extends RouteBuilder {

	@Autowired
	private EmployeesService service;

	@Autowired
	private EmployeeProcessor processor;

	@Override
	public void configure() throws Exception {

		onException(NoSuchElementException.class)
			.handled(true)
//	    	.transform().simple("${exception.message}");
//			.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.NOT_FOUND.value()));
			.process(new Processor() {

				@Override
				public void process(Exchange exchange) throws Exception {
					String code = exchange.getIn().getHeader("code", String.class);

					exchange.getIn().removeHeader("*");
					exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_FOUND.value());
					exchange.getIn().setBody(new MessageDto("Funcionário com código " + code + " não encontrado no BD"));
				}

			});

		restConfiguration().component("servlet").host("localhost").port(8080).bindingMode(RestBindingMode.auto);

		rest("/api/V1/says/hello").get().produces(MediaType.APPLICATION_JSON_VALUE).to("direct:hello");

		rest("/api/V1/employees").produces(MediaType.APPLICATION_JSON_VALUE).consumes(MediaType.APPLICATION_JSON_VALUE)
				.get()
//					.outType(Employee.class)
					.to("direct:get-employees")
				.get("/{code}").outType(EmployeeDTO.class)
					.to("direct:get-employee")
				.post().type(EmployeeDTO.class)
					.to("direct:add-employee").outType(EmployeeDTO.class)
				.put("/{code}").type(EmployeeDTO.class)
					.to("direct:update-employee").outType(EmployeeDTO.class)
				.delete("/{code}")
					.to("direct:delete-employee");

		from("direct:hello").routeId("welcome").setBody(constant("Hello world"));

		from("direct:get-employees").routeId("employees-list").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				List<EmployeeDTO> employees = service.getEmployees();

				Message message = new DefaultMessage(exchange.getContext());
				message.setBody(employees);
				exchange.setMessage(message);

			}

		});

		from("direct:get-employee").routeId("employee-one").doTry().process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				EmployeeDTO employee = service.getEmployee(exchange.getIn().getHeader("code", String.class));

				exchange.getIn().setBody(employee);

			}

		});

		from("direct:add-employee").routeId("add-employee")
			.doTry().process(processor)
			.doCatch(DataIntegrityViolationException.class).process(new Processor() {

				@Override
				public void process(Exchange exchange) throws Exception {
					exchange.getIn().removeHeaders("*");
					exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
					
					EmployeeDTO dto = exchange.getIn().getBody(EmployeeDTO.class);
					exchange.getIn().setBody(new MessageDto("Funcionário com o CPF " + dto.getCpf() + " já existe no BD"));
						
				}
					
			});

		from("direct:update-employee").routeId("update-employee").doTry().process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				EmployeeDTO body = exchange.getIn().getBody(EmployeeDTO.class);
				String code = exchange.getIn().getHeader("code", String.class);

				EmployeeDTO employee = service.updateEmployee(body, code);

				exchange.getIn().setBody(employee);

			}

		}).doCatch(DataIntegrityViolationException.class).process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				exchange.getIn().removeHeaders("*");
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
				
				EmployeeDTO dto = exchange.getIn().getBody(EmployeeDTO.class);
				exchange.getIn().setBody(new MessageDto("Funcionário com o CPF " + dto.getCpf() + " já existe no BD"));
					
			}
				
		});

		from("direct:delete-employee").routeId("delete-employee").doTry().process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				service.deleteEmployee(exchange.getIn().getHeader("code", String.class));

				exchange.getIn().removeHeaders("*");
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NO_CONTENT.value());
			}

		});
	}

}
