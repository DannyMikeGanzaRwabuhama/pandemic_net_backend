package com.app.pandemicnet;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "PandemicNet",
				description = "backend documentation for pandemicNet",
				version = "v1.0.0"
		)
)
public class PandemicnetApplication {

	public static void main(String[] args) {
		SpringApplication.run(PandemicnetApplication.class, args);
	}

}
