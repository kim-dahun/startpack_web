package com.upmudoum.auth.domain.auth.repository;

import com.upmudoum.auth.domain.auth.entity.OpenApiClient;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpenApiClientRepository extends JpaRepository<OpenApiClient, Long> {

    Optional<OpenApiClient> findByClientIdAndEnabledTrue(String clientId);
}
