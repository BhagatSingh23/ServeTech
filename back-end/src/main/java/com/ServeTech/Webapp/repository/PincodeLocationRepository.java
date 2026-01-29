package com.ServeTech.Webapp.repository;

import com.ServeTech.Webapp.entity.PincodeLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PincodeLocationRepository extends JpaRepository<PincodeLocation, Long> {

    Optional<PincodeLocation> findByPincode(String pincode);

    boolean existsByPincode(String pincode);
}