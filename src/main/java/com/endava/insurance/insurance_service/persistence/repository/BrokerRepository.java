package com.endava.insurance.insurance_service.persistence.repository;

import com.endava.insurance.insurance_service.domain.model.Broker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BrokerRepository extends JpaRepository<Broker, Long> {

    Optional<Broker> findByBrokerCode(String brokerCode);

    boolean existsByBrokerCode(String brokerCode);

    Optional<Broker> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhone(String phone);

}
