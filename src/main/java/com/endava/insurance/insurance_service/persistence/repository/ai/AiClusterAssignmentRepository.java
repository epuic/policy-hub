package com.endava.insurance.insurance_service.persistence.repository.ai;

import com.endava.insurance.insurance_service.domain.enums.AiClusterTarget;
import com.endava.insurance.insurance_service.domain.model.ai.AiClusterAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiClusterAssignmentRepository extends JpaRepository<AiClusterAssignment, Long> {

    Optional<AiClusterAssignment> findByTargetAndEntityId(AiClusterTarget target, Long entityId);

    List<AiClusterAssignment> findByTargetOrderByClusterIdAscEntityIdAsc(AiClusterTarget target);
}
