package com.example.realestate.repository;

import com.example.realestate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query(value = "SELECT EXTRACT(MONTH FROM created_at) as month, COUNT(*) FROM users WHERE EXTRACT(YEAR FROM created_at) = :year GROUP BY month", nativeQuery = true)
    List<Object[]> getRegistrationsByMonth(@Param("year") int year);
}