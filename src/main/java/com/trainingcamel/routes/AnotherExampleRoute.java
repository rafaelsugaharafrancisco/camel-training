package com.trainingcamel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.http.HttpMethod;


//@Component
public class AnotherExampleRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		restConfiguration()
			.component("servlet").host("localhost").port(8080)
			.bindingMode(RestBindingMode.off);
//				.dataFormatProperty("json.in.disableFeatures", "FAIL_ON_EMPTY_BEANS");
		
		rest("/api/V2/employees")
//			.produces(MediaType.APPLICATION_JSON_VALUE)
//			.consumes(MediaType.APPLICATION_JSON_VALUE)
			.get().to("direct:employees-list");
		
		from("direct:employees-list")
			.routeId("get-all")
			.removeHeader(Exchange.HTTP_URI)
			.setHeader(Exchange.HTTP_METHOD, constant(HttpMethod.GET))
			.to("http://localhost:8081/employees");
	}

}
