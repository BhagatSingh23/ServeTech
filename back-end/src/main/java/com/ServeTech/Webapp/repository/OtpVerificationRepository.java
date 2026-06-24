package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findTopByPhoneNumberAndPurposeOrderByCreatedAtDesc(
            String phoneNumber, String purpose
    );

    Optional<OtpVerification> findTopByEmailAndPurposeOrderByCreatedAtDesc(
            String email, String purpose
    );

    void deleteByExpiryTimeBefore(LocalDateTime dateTime);
}