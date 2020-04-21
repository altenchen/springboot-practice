package com.springboot.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class SpringbootParcticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootParcticeApplication.class, args);
	}

}
