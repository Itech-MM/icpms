package org.flexitech.projects.icpms.persistence.repositories.external_client;

import java.util.List;

import org.flexitech.projects.icpms.persistence.entities.external_client.ExternalClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalClientRepository extends JpaRepository<ExternalClient, Long> {

	List<ExternalClient> findByStatus(Integer status);
}