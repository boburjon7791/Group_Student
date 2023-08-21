package com.example.demo;

import com.example.demo.entities.Student;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		/*
		* task ni oxirida aytilgan kutubxona orqali log bilan ishlay olmadim
		* */
		for (Student.Gender value : Student.Gender.values()) {
			System.out.println("value = " + value);
			System.out.println("value = " + value.name());
		}
	}

}
