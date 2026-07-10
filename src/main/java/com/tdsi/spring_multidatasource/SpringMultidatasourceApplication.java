package com.tdsi.spring_multidatasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringMultidatasourceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMultidatasourceApplication.class, args);
	}

}
