package com.lifelink.lifelink.repository;

import com.lifelink.lifelink.model.EligibilityAssessment;
import com.lifelink.lifelink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EligibilityAssessmentRepository extends JpaRepository<EligibilityAssessment, Long> {
    Optional<EligibilityAssessment> findFirstByDonorOrderByAssessedAtDesc(User donor);

    java.util.List<EligibilityAssessment> findAllByOrderByAssessedAtDesc();
}
