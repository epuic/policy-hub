package com.endava.insurance.insurance_service.persistence.repository;

import com.endava.insurance.insurance_service.domain.model.auth.AdministratorAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministratorAuthRepository extends JpaRepository<AdministratorAuth, Long> {

    Optional<AdministratorAuth> findByEmail(String email);

    boolean existsByEmail(String email);
}
