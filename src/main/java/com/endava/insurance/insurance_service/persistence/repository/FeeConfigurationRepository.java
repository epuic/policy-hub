package com.endava.insurance.insurance_service.persistence.repository;

import com.endava.insurance.insurance_service.domain.model.metadata.FeeConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeeConfigurationRepository extends JpaRepository<FeeConfiguration, Long> {

    @Query("SELECT f FROM FeeConfiguration f WHERE f.active = true AND " +
            "(f.effectiveFrom IS NULL OR f.effectiveFrom <= :date) AND " +
            "(f.effectiveTo IS NULL OR f.effectiveTo >= :date)")
    List<FeeConfiguration> findActiveForDate(@Param("date") LocalDate date);
}
