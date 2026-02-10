package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.request.LoginRequest;
import com.ServeTech.Webapp.dto.request.ResetPasswordRequest;
import com.ServeTech.Webapp.dto.request.SignupRequest;
import com.ServeTech.Webapp.dto.response.AuthResponse;
import com.ServeTech.Webapp.dto.response.UserResponse;
import com.ServeTech.Webapp.entity.*;
import com.ServeTech.Webapp.entity.enums.AccountStatus;
import com.ServeTech.Webapp.entity.enums.RoleType;
import com.ServeTech.Webapp.exception.CustomException;
import com.ServeTech.Webapp.repository.*;
import com.ServeTech.Webapp.security.JwtTokenProvider;
import com.ServeTech.Webapp.util.UniqueIdGenerator;
import com.ServeTech.Webapp.util.UsernameGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// AuthService class to handle authentication and authorization
// We can also utilize Aspect for this
@Service
public class AuthService {

    private final UserRepository userRepository;


    private final RoleRepository roleRepository;


    private final WorkerProfileRepository workerProfileRepository;


    private final ClientProfileRepository clientProfileRepository;


    private final OtpService otpService;


    private final PasswordEncoder passwordEncoder;


    private PincodeService pincodeService;


    private final JwtTokenProvider jwtTokenProvider;


    private final UniqueIdGenerator uniqueIdGenerator;


    private final UsernameGenerator usernameGenerator;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       WorkerProfileRepository workerProfileRepository,
                       ClientProfileRepository clientProfileRepository, OtpService otpService,
                       PasswordEncoder passwordEncoder, PincodeService pincodeService
            , JwtTokenProvider jwtTokenProvider,
                       UniqueIdGenerator uniqueIdGenerator, UsernameGenerator usernameGenerator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.clientProfileRepository = clientProfileRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
        this.pincodeService = pincodeService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.uniqueIdGenerator = uniqueIdGenerator;
        this.usernameGenerator = usernameGenerator;
    }

    // Register user
    @Transactional
    public AuthResponse signup(SignupRequest request) {
        // 1. Verify OTP first
        boolean otpValid = otpService.verifyOtp(
                request.getPhoneNumber(),
                request.getOtp(),
                "REGISTRATION"
        );

        if (!otpValid) {
            throw new CustomException("Invalid or expired OTP");
        }

        // 2. Check if phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new CustomException("Phone number already registered");
        }

//        // 3. Validate and get pincode location
//        PincodeLocation location = pincodeLocationRepository
//                .findByPincode(request.getPincode())
//                .orElseThrow(() -> new CustomException("Invalid pincode"));

        // 4. Get role
        Role role = roleRepository.findByName(String.valueOf(request.getRole()))
                .orElseThrow(() -> new CustomException("Role not found"));

        // 5. Generate unique user ID
        String uniqueUserId = uniqueIdGenerator.generateUniqueUserId();

        // 6. Generate username
        String username = usernameGenerator.generateUsername(
                request.getFirstName(),
                request.getLastName(),
                uniqueUserId
        );

        // 7. Check if username already exists (unlikely but possible)
        int suffix = 1;
        String finalUsername = username;
        while (userRepository.existsByUsername(finalUsername)) {
            finalUsername = username + suffix;
            suffix++;
        }

        // Fetch the location from the pincode
        Location location = pincodeService.fetchLocation(request.getPincode());

        // 8. Create user
        User user = new User();
        user.setUniqueUserId(uniqueUserId);
        user.setUsername(finalUsername);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPincode(request.getPincode());
        user.setBlock(location.getBlock());
        user.setDistrict(location.getDistrict());
        user.setState(location.getState());
        user.setPhoneVerified(true); // OTP verified
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.addRole(role);

        // 9. Save user
        user = userRepository.save(user);

        // 10. Create profile based on role
        if (RoleType.ROLE_WORKER.equals(request.getRole())) {
            WorkerProfile workerProfile = new WorkerProfile(user);
            workerProfileRepository.save(workerProfile);
        } else if (RoleType.ROLE_CLIENT.equals(request.getRole())) {
            ClientProfile clientProfile = new ClientProfile(user);
            clientProfileRepository.save(clientProfile);
        }

        // 11. Generate JWT token
        String token = jwtTokenProvider.generateToken(user);

        // 12. Return response
        return new AuthResponse(token, new UserResponse(user));
    }

    // Login user
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Find user by username or phone number
        User user = findUserByUsernameOrPhone(request.getUsernameOrPhone());

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid credentials");
        }

        // Check if account is active
        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new CustomException("Account is " + user.getAccountStatus().toString().toLowerCase());
        }

        // Update last login
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);

        // Generate token
        String token = jwtTokenProvider.generateToken(user);

        return new AuthResponse(token, new UserResponse(user));
    }

    // Reset password method
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 1. Verify OTP
        boolean otpValid = otpService.verifyOtp(
                request.getPhoneNumber(),
                request.getOtp(),
                "PASSWORD_RESET"
        );

        if (!otpValid) {
            throw new CustomException("Invalid or expired OTP");
        }

        // 2. Find user by phone number
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new CustomException("User not found"));

        // 3. Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // This method is used by other services to find user by username or phone number
    // It will throw CustomException if user is not found
    private User findUserByUsernameOrPhone(String usernameOrPhone) {
        // Check if it's a phone number (10 digits)
        if (usernameOrPhone.matches("^[6-9]\\d{9}$")) {
            return userRepository.findByPhoneNumber(usernameOrPhone)
                    .orElseThrow(() -> new CustomException("User not found"));
        } else {
            return userRepository.findByUsername(usernameOrPhone)
                    .orElseThrow(() -> new CustomException("User not found"));
        }
    }
}