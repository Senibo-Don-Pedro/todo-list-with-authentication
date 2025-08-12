package com.senibo.todo_list_with_authentication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Todo API with JWT",
				version = "1.0.0",
				description = "JWT-secured todo API with user signup/login and role-aware access.",
				contact = @Contact(name = "Don‑Pedro Senibo", email = "senibodonpedro@gmail.com")
		),
		// 👇 This makes Swagger send Authorization for all operations by default
		security = { @SecurityRequirement(name = "bearerAuth") }
)
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER
)
public class TodoListWithAuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoListWithAuthenticationApplication.class, args);
	}

}
