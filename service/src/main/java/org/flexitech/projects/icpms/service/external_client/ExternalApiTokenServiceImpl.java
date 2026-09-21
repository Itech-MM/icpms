package org.flexitech.projects.icpms.service.external_client;

import java.util.List;
import java.util.UUID;

import org.flexitech.projects.icpms.common.CommonValidators;
import org.flexitech.projects.icpms.common.enums.ActiveStatus;
import org.flexitech.projects.icpms.persistence.entities.external_client.ExternalClient;
import org.flexitech.projects.icpms.persistence.repositories.external_client.ExternalClientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApiTokenServiceImpl implements ExternalApiTokenService{
	
	private final ExternalClientRepository externalApiClientRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public boolean isValidToken(String rawToken) {
		if (!CommonValidators.validString(rawToken)) return false;

		List<ExternalClient> activeClients = externalApiClientRepository.findByStatus(ActiveStatus.ACTIVE.getCode());
		for (ExternalClient client : activeClients) {
			if (passwordEncoder.matches(rawToken, client.getTokenHash())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String generateToken(String clientName) {
		String rawToken = UUID.randomUUID().toString().replace("-", "")
				+ UUID.randomUUID().toString().replace("-", "");

		ExternalClient client = new ExternalClient();
		client.setName(clientName);
		client.setTokenHash(passwordEncoder.encode(rawToken));
		client.setStatus(ActiveStatus.ACTIVE.getCode());
		externalApiClientRepository.save(client);

		return rawToken;
	}

}
