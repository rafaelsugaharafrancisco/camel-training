package com.trainingcamel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.trainingcamel.dto.MessageDto;


@Component
public class AnotherExampleRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		
		onException(HttpOperationFailedException.class)
			.handled(true)
			.choice()
				.when(exceptionMessage().contains("404")).to("direct:status-code-404")
				.when(exceptionMessage().contains("400")).to("direct:status-code-400")
				.otherwise()
					.setHeader(Exchange.HTTP_RESPONSE_CODE, constant(500))
					.setBody(constant("Um erro ocorreu")).marshal().json(JsonLibrary.Jackson, String.class)
			.endChoice();
		
		restConfiguration()
			.component("servlet").host("localhost").port(8080)
			.bindingMode(RestBindingMode.off);
//				.dataFormatProperty("json.in.disableFeatures", "FAIL_ON_EMPTY_BEANS");
		
		rest("/api/V2/employees")
//			.produces(MediaType.APPLICATION_JSON_VALUE)
//			.consumes(MediaType.APPLICATION_JSON_VALUE)
			.get().to("direct:employees-list")
			.get("/{code}").to("direct:employees-one")
			.post().to("direct:employees-add");
		
		from("direct:employees-list")
			.routeId("get-all")
			.removeHeader(Exchange.HTTP_URI)
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
			.to("http://localhost:8081/employees");
		
		from("direct:employees-one")
			.routeId("get-one")
			.removeHeader(Exchange.HTTP_URI)
//			.setHeader(Exchange.HTTP_PATH, simple("${header.code}"))
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
			.toD("http://localhost:8081/employees/${header.code}").log("${body}");
//				.log("${header.CamelHttpResponseCode}");
		
		from("direct:employees-add")
			.routeId("add-one")
			.removeHeader(Exchange.HTTP_URI)
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.POST))
			.to("http://localhost:8081/employees");
		
		from("direct:status-code-404").routeId("not-found").process(new Processor() {
				
			@Override
			public void process(Exchange exchange) throws Exception {
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_FOUND.value());
				exchange.getIn().setBody(new MessageDto("Não encontrado!"));
			}
				
		}).marshal().json(JsonLibrary.Jackson, MessageDto.class);
		
		from("direct:status-code-400").routeId("bad-request").process(new Processor() {
			
			@Override
			public void process(Exchange exchange) throws Exception {
				exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST.value());
				exchange.getIn().setBody(new MessageDto("Algum campo inválido ou CPF já cadastrado!"));
			}
				
		}).marshal().json(JsonLibrary.Jackson, MessageDto.class);
	}
}
