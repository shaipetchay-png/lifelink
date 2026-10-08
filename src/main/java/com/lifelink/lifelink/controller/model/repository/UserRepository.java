package com.lifelink.lifelink.controller.model.repository;

import com.lifelink.lifelink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRoleIgnoreCaseOrderByIdDesc(String role);

    @Query("""
        SELECT u FROM User u
        WHERE UPPER(u.role) = 'DONOR'
        AND (
            LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.middleName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.contact) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(u.bloodType) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        ORDER BY u.id DESC
        """)
    List<User> searchDonors(@Param("keyword") String keyword);
}
