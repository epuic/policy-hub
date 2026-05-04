package com.endava.insurance.insurance_service.persistence.repository;

import com.endava.insurance.insurance_service.domain.model.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {

    @EntityGraph(attributePaths = {"owner", "city", "city.county", "city.county.country", "riskFactors"})
    Optional<Building> findById(Long id);

    @EntityGraph(attributePaths = {"owner", "city", "city.county", "city.county.country", "riskFactors"})
    Page<Building> findByOwnerId(Long clientId, Pageable pageable);

    @EntityGraph(attributePaths = {"owner", "city", "city.county", "city.county.country", "riskFactors"})
    Page<Building> findByCityId(Long cityId, Pageable pageable);
}
