package com.lifelink.lifelink.repository;

import com.lifelink.lifelink.model.Donation;
import com.lifelink.lifelink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    long countByDonor(User donor);
    long countByStatusIgnoreCase(String status);
    List<Donation> findByDonorOrderByDonationDateDesc(User donor);
}
