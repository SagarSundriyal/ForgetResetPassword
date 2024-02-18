package com.reset.password.demo.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan("com.reset.password.demo.app")
@EntityScan("com.reset.password.demo.app")
@EnableJpaRepositories({ "com.reset.password.demo.app" })
public class ResetPasswordDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResetPasswordDemoApplication .class, args);
	}
}
