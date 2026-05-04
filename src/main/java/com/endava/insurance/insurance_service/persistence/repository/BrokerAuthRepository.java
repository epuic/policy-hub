package com.endava.insurance.insurance_service.persistence.repository;

import com.endava.insurance.insurance_service.domain.model.auth.BrokerAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrokerAuthRepository extends JpaRepository<BrokerAuth, Long> {

    Optional<BrokerAuth> findByEmail(String email);

    Optional<BrokerAuth> findByBrokerId(Long brokerId);

    boolean existsByEmail(String email);
}
