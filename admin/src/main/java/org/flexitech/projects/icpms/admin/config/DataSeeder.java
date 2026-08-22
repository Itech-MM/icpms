package org.flexitech.projects.icpms.admin.config;

import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.persistence.entities.role.Role;
import org.flexitech.projects.icpms.persistence.entities.user.User;
import org.flexitech.projects.icpms.persistence.repositories.role.RoleRepository;
import org.flexitech.projects.icpms.persistence.repositories.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Seeds a default ADMIN role and user on first run so the admin console can
 * be logged into out of the box.
 *
 * Default login: 0123456789 / admin123 (please change after first login)
 */
@Component
@Slf4j
public class DataSeeder implements CommandLineRunner {

	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		Role adminRole = roleRepository.findByStatus(ActiveStatus.ACTIVE.getCode()).stream()
				.filter(r -> "ADMIN".equalsIgnoreCase(r.getCode()))
				.findFirst()
				.orElseGet(() -> {
					Role role = new Role();
					role.setName("Administrator");
					role.setCode("ADMIN");
					role.setStatus(ActiveStatus.ACTIVE.getCode());
					return roleRepository.save(role);
				});

		if (userRepository.findByPhoneNumber("0123456789").isEmpty()) {
			User user = new User();
			user.setName("System Administrator");
			user.setPhoneNumber("0123456789");
			user.setPassword(passwordEncoder.encode("admin123"));
			user.setStatus(ActiveStatus.ACTIVE.getCode());
			user.setRole(adminRole);
			userRepository.save(user);
			log.info("===================================================");
			log.info(" Default admin account created!");
			log.info(" Login (Phone Number): 0123456789");
			log.info(" Password: admin123");
			log.info(" Please change this password after first login.");
			log.info("===================================================");
		}
	}
}
