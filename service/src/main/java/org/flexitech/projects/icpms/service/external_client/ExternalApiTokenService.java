package org.flexitech.projects.icpms.service.external_client;

public interface ExternalApiTokenService {
	boolean isValidToken(String rawToken);
	String generateToken(String clientName);
}
