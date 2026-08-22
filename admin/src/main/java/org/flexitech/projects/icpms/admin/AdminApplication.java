package org.flexitech.projects.icpms.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.flexitech.projects.icpms")
@EntityScan(basePackages = "org.flexitech.projects.icpms.persistence.entities")
@EnableJpaRepositories(basePackages = "org.flexitech.projects.icpms.persistence.repositories")
public class AdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class, args);
	}

}
