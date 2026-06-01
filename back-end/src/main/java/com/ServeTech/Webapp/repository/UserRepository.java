package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.User;
import com.ServeTech.Webapp.entity.enums.AccountStatus;
import com.ServeTech.Webapp.entity.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByUsername(String username);

    Optional<User> findByUniqueUserId(String uniqueUserId);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") RoleType roleName);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") RoleType roleName);

    List<User> findByAccountStatus(AccountStatus accountStatus);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.accountStatus = :status")
    List<User> findByRoleNameAndAccountStatus(@Param("roleName") RoleType roleName, @Param("status") AccountStatus status);
}