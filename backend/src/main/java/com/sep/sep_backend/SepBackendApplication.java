package com.sep.sep_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.sep.sep_backend.user.repository.UserRepository;

@SpringBootApplication
public class SepBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SepBackendApplication.class, args);
	
	}
 @Bean
	CommandLineRunner run(UserRepository userRepository) {
		return args -> {
			System.out.println("Hello, World!");
		};
	}


}


