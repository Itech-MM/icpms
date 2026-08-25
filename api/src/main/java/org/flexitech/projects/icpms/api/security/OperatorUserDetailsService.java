package org.flexitech.projects.icpms.api.security;

import org.flexitech.projects.icpms.persistence.entities.operator.Operator;
import org.flexitech.projects.icpms.persistence.repositories.operator.OperatorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class OperatorUserDetailsService implements UserDetailsService {

	private final OperatorRepository operatorRepository;

	public OperatorUserDetailsService(OperatorRepository operatorRepository) {
		this.operatorRepository = operatorRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Operator operator = operatorRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException("Operator not found!"));
		return new OperatorPrincipal(operator);
	}
}