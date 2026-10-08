package com.lifelink.lifelink.repository;

import com.lifelink.lifelink.model.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {

    List<BloodRequest> findTop10ByOrderByCreatedAtDesc();

    List<BloodRequest> findTop10ByBloodTypeOrderByCreatedAtDesc(String bloodType);

}