package com.endava.insurance.insurance_service.persistence.repository;

import com.endava.insurance.insurance_service.domain.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByIdentificationNumber(String identificationNumber);

    Page<Client> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByIdentificationNumber(String identificationNumber);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phoneNumber);
}
