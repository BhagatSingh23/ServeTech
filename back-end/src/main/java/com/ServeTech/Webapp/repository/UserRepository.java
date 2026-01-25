package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByUsername(String username);

    Optional<User> findByUniqueUserId(String uniqueUserId);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);
}