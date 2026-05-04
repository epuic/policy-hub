package com.endava.insurance.insurance_service.persistence.repository;

import com.endava.insurance.insurance_service.domain.enums.PolicyStatus;
import com.endava.insurance.insurance_service.domain.model.Policy;
import com.endava.insurance.insurance_service.persistence.projection.PolicyReportByBrokerProjection;
import com.endava.insurance.insurance_service.persistence.projection.PolicyReportByCityProjection;
import com.endava.insurance.insurance_service.persistence.projection.PolicyReportByCountryProjection;
import com.endava.insurance.insurance_service.persistence.projection.PolicyReportByCountyProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Override
    @EntityGraph(attributePaths = {"client", "building", "building.city", "building.city.county", "building.city.county.country", "broker", "currency"})
    Optional<Policy> findById(Long id);

    @EntityGraph(attributePaths = {"client", "building", "building.city", "building.city.county", "building.city.county.country", "broker", "currency"})
    @Query("SELECT p FROM Policy p WHERE " +
            "(:clientId IS NULL OR p.client.id = :clientId) AND " +
            "(:brokerId IS NULL OR p.broker.id = :brokerId) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:startDateFrom IS NULL OR p.endDate >= :startDateFrom) AND " +
            "(:endDateTo IS NULL OR p.startDate <= :endDateTo)")
    Page<Policy> findFiltered(
            @Param("clientId") Long clientId,
            @Param("brokerId") Long brokerId,
            @Param("status") PolicyStatus status,
            @Param("startDateFrom") LocalDate startDateFrom,
            @Param("endDateTo") LocalDate endDateTo,
            Pageable pageable);

    boolean existsByCurrencyIdAndStatus(Long currencyId, PolicyStatus status);

    @Query("SELECT p FROM Policy p WHERE p.status = :status AND p.endDate < :currentDate")
    List<Policy> findActivePoliciesExpiredBefore(@Param("status") PolicyStatus status, @Param("currentDate") LocalDate currentDate);

    @EntityGraph(attributePaths = {"currency"})
    List<Policy> findByBuildingIdOrderByStartDateDesc(Long buildingId);


    @Query(nativeQuery = true, value = """
            SELECT ct.name AS "countryName", c.code AS "currencyCode", COUNT(p.id) AS "policyCount",
                   SUM(p.final_premium) AS "totalPremium",
                   SUM(p.final_premium) * c.exchange_rate_to_base AS "totalInBase"
            FROM policies p
            JOIN building b ON p.building_id = b.id
            JOIN city ci ON b.city_id = ci.id
            JOIN county co ON ci.county_id = co.id
            JOIN country ct ON co.country_id = ct.id
            JOIN currency c ON p.currency_id = c.id
            WHERE (CAST(:fromDate AS DATE) IS NULL OR p.start_date >= CAST(:fromDate AS DATE))
              AND (CAST(:toDate AS DATE) IS NULL OR p.start_date <= CAST(:toDate AS DATE))
              AND (:status IS NULL OR p.status = :status)
              AND (:currencyCode IS NULL OR c.code = :currencyCode)
              AND (:buildingType IS NULL OR b.type = :buildingType)
            GROUP BY ct.name, c.code, c.exchange_rate_to_base
            """)
    List<PolicyReportByCountryProjection> findReportByCountry(
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("status") String status,
            @Param("currencyCode") String currencyCode,
            @Param("buildingType") String buildingType);


    @Query(nativeQuery = true, value = """
            SELECT ct.name AS "countryName", co.name AS "countyName", c.code AS "currencyCode",
                   COUNT(p.id) AS "policyCount", SUM(p.final_premium) AS "totalPremium",
                   SUM(p.final_premium) * c.exchange_rate_to_base AS "totalInBase"
            FROM policies p
            JOIN building b ON p.building_id = b.id
            JOIN city ci ON b.city_id = ci.id
            JOIN county co ON ci.county_id = co.id
            JOIN country ct ON co.country_id = ct.id
            JOIN currency c ON p.currency_id = c.id
            WHERE (CAST(:fromDate AS DATE) IS NULL OR p.start_date >= CAST(:fromDate AS DATE))
              AND (CAST(:toDate AS DATE) IS NULL OR p.start_date <= CAST(:toDate AS DATE))
              AND (:status IS NULL OR p.status = :status)
              AND (:currencyCode IS NULL OR c.code = :currencyCode)
              AND (:buildingType IS NULL OR b.type = :buildingType)
            GROUP BY ct.name, co.name, c.code, c.exchange_rate_to_base
            """)
    List<PolicyReportByCountyProjection> findReportByCounty(
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("status") String status,
            @Param("currencyCode") String currencyCode,
            @Param("buildingType") String buildingType);


    @Query(nativeQuery = true, value = """
            SELECT ct.name AS "countryName", co.name AS "countyName", ci.name AS "cityName",
                   c.code AS "currencyCode", COUNT(p.id) AS "policyCount", SUM(p.final_premium) AS "totalPremium",
                   SUM(p.final_premium) * c.exchange_rate_to_base AS "totalInBase"
            FROM policies p
            JOIN building b ON p.building_id = b.id
            JOIN city ci ON b.city_id = ci.id
            JOIN county co ON ci.county_id = co.id
            JOIN country ct ON co.country_id = ct.id
            JOIN currency c ON p.currency_id = c.id
            WHERE (CAST(:fromDate AS DATE) IS NULL OR p.start_date >= CAST(:fromDate AS DATE))
              AND (CAST(:toDate AS DATE) IS NULL OR p.start_date <= CAST(:toDate AS DATE))
              AND (:status IS NULL OR p.status = :status)
              AND (:currencyCode IS NULL OR c.code = :currencyCode)
              AND (:buildingType IS NULL OR b.type = :buildingType)
            GROUP BY ct.name, co.name, ci.name, c.code, c.exchange_rate_to_base
            """)
    List<PolicyReportByCityProjection> findReportByCity(
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("status") String status,
            @Param("currencyCode") String currencyCode,
            @Param("buildingType") String buildingType);


    @Query(nativeQuery = true, value = """
            SELECT br.name AS "brokerName", c.code AS "currencyCode", COUNT(p.id) AS "policyCount",
                   SUM(p.final_premium) AS "totalPremium",
                   SUM(p.final_premium) * c.exchange_rate_to_base AS "totalInBase"
            FROM policies p
            JOIN brokers br ON p.broker_id = br.id
            JOIN currency c ON p.currency_id = c.id
            JOIN building b ON p.building_id = b.id
            WHERE (CAST(:fromDate AS DATE) IS NULL OR p.start_date >= CAST(:fromDate AS DATE))
              AND (CAST(:toDate AS DATE) IS NULL OR p.start_date <= CAST(:toDate AS DATE))
              AND (:status IS NULL OR p.status = :status)
              AND (:currencyCode IS NULL OR c.code = :currencyCode)
              AND (:buildingType IS NULL OR b.type = :buildingType)
            GROUP BY br.name, c.code, c.exchange_rate_to_base
            """)
    List<PolicyReportByBrokerProjection> findReportByBroker(
            @Param("fromDate") String fromDate,
            @Param("toDate") String toDate,
            @Param("status") String status,
            @Param("currencyCode") String currencyCode,
            @Param("buildingType") String buildingType);
}
