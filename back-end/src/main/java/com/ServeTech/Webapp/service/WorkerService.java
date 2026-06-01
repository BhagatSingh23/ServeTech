package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.request.UpdateWorkerProfileRequest;
import com.ServeTech.Webapp.dto.response.WorkerProfileResponse;
import com.ServeTech.Webapp.entity.Skill;
import com.ServeTech.Webapp.entity.User;
import com.ServeTech.Webapp.entity.WorkerProfile;
import com.ServeTech.Webapp.exception.CustomException;
import com.ServeTech.Webapp.repository.SkillRepository;
import com.ServeTech.Webapp.repository.UserRepository;
import com.ServeTech.Webapp.repository.WorkerProfileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class WorkerService {

    private final WorkerProfileRepository workerProfileRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public WorkerService(WorkerProfileRepository workerProfileRepository,
                         UserRepository userRepository,
                         SkillRepository skillRepository) {
        this.workerProfileRepository = workerProfileRepository;
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    public WorkerProfileResponse getProfile(Long userId) {
        WorkerProfile profile = workerProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    // Auto-create profile if missing
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
                    WorkerProfile newProfile = new WorkerProfile(user);
                    return workerProfileRepository.save(newProfile);
                });
        return WorkerProfileResponse.fromEntity(profile);
    }

    @Transactional
    public WorkerProfileResponse updateProfile(Long userId, UpdateWorkerProfileRequest request) {
        WorkerProfile profile = workerProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User u = userRepository.findById(userId)
                            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
                    return workerProfileRepository.save(new WorkerProfile(u));
                });

        User user = profile.getUser();

        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getDailyWage() != null) {
            profile.setDailyWage(request.getDailyWage());
        }
        if (request.getExperienceYears() != null) {
            profile.setExperienceYears(request.getExperienceYears());
        }
        if (request.getPincode() != null) {
            user.setPincode(request.getPincode());
            userRepository.save(user);
        }
        if (request.getSkillIds() != null && !request.getSkillIds().isEmpty()) {
            Set<Skill> skills = new HashSet<>(skillRepository.findAllById(request.getSkillIds()));
            profile.setSkills(skills);
        }

        WorkerProfile saved = workerProfileRepository.save(profile);
        return WorkerProfileResponse.fromEntity(saved);
    }

    @Transactional
    public WorkerProfileResponse toggleAvailability(Long userId) {
        WorkerProfile profile = workerProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User u = userRepository.findById(userId)
                            .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
                    return workerProfileRepository.save(new WorkerProfile(u));
                });

        profile.setAvailableForWork(!profile.getAvailableForWork());
        WorkerProfile saved = workerProfileRepository.save(profile);
        return WorkerProfileResponse.fromEntity(saved);
    }
}
