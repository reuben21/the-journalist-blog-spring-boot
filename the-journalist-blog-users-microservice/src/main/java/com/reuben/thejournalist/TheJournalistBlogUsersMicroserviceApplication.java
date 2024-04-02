package com.reuben.thejournalist;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class TheJournalistBlogUsersMicroserviceApplication {


	public static void main(String[] args) {
		SpringApplication.run(TheJournalistBlogUsersMicroserviceApplication.class, args);
	}


//	@Bean
//	CommandLineRunner runner(UserRepository userRepository) {
//		return args -> {
////			User user = new User("Reuben", "reuben21@gmail.com", "password", Roles.ADMIN);
////			userRepository.insert(user);
//
//			System.out.println("Application is Now Running ");
//		};
//	}

}
