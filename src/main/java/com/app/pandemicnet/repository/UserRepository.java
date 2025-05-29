package com.app.pandemicnet.repository;

import com.app.pandemicnet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByUniqueId(UUID uniqueId);
    Optional<User> findUserByUniqueId(UUID userUniqueId);
}
