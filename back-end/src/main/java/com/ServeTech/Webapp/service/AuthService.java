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
import com.ServeTech.Webapp.repository.ClientProfileRepository;
import com.ServeTech.Webapp.repository.RoleRepository;
import com.ServeTech.Webapp.repository.UserRepository;
import com.ServeTech.Webapp.repository.WorkerProfileRepository;
import com.ServeTech.Webapp.security.JwtTokenProvider;
import com.ServeTech.Webapp.util.UniqueIdGenerator;
import com.ServeTech.Webapp.util.UsernameGenerator;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Collections;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final PincodeService pincodeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UniqueIdGenerator uniqueIdGenerator;
    private final UsernameGenerator usernameGenerator;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       WorkerProfileRepository workerProfileRepository,
                       ClientProfileRepository clientProfileRepository,
                       OtpService otpService,
                       PasswordEncoder passwordEncoder,
                       PincodeService pincodeService,
                       JwtTokenProvider jwtTokenProvider,
                       UniqueIdGenerator uniqueIdGenerator,
                       UsernameGenerator usernameGenerator) {
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
    public AuthResponse signup(@Valid @RequestBody SignupRequest request) {
        boolean isPhoneSignup = request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty();
        boolean isEmailSignup = request.getEmail() != null && !request.getEmail().isEmpty();

        if (!isPhoneSignup && !isEmailSignup) {
            throw new CustomException("Phone number or email is required for signup");
        }

        boolean phoneVerified = isPhoneSignup && otpService.isOtpAlreadyVerified(request.getPhoneNumber(), "REGISTRATION");
        boolean emailVerified = isEmailSignup && otpService.isEmailOtpAlreadyVerified(request.getEmail(), "REGISTRATION");

        if (!phoneVerified && !emailVerified) {
            throw new CustomException("Please verify your contact (email or phone) with OTP first.");
        }

        if (isPhoneSignup && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new CustomException("Phone number already registered");
        }

        if (isEmailSignup && userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already registered");
        }

        // 3. CONVERT STRING TO ENUM (Fixes the "incompatible type" error)
        RoleType roleType;
        try {
            // Converts "ROLE_WORKER" (String) -> RoleType.ROLE_WORKER (Enum)
            roleType = RoleType.valueOf(request.getRole());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomException("Invalid Role: " + request.getRole());
        }

        // 4. Get Role Entity using the Enum
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new CustomException("Role not found in database: " + roleType));

        // 5. Generate unique user ID
        String uniqueUserId = uniqueIdGenerator.generateUniqueUserId();

        // 6. Generate username
        String username = usernameGenerator.generateUsername(
                request.getFirstName(),
                request.getLastName(),
                uniqueUserId
        );

        // 7. Check if username already exists (handle duplicates)
        int suffix = 1;
        String finalUsername = username;
        while (userRepository.existsByUsername(finalUsername)) {
            finalUsername = username + suffix;
            suffix++;
        }

        // 8. Resolve location: prefer values from request, fallback to pincode API, then defaults
        String block = request.getBlock();
        String district = request.getDistrict();
        String state = request.getState();

        if (block == null || block.isBlank() || district == null || district.isBlank() || state == null || state.isBlank()) {
            try {
                Location location = pincodeService.fetchLocation(request.getPincode());
                if (location != null) {
                    if (block == null || block.isBlank()) block = location.getBlock();
                    if (district == null || district.isBlank()) district = location.getDistrict();
                    if (state == null || state.isBlank()) state = location.getState();
                }
            } catch (Exception e) {
                System.err.println("Pincode API failed: " + e.getMessage());
            }
        }

        // Final fallback for any still-missing fields
        if (block == null || block.isBlank()) block = "Unknown Area";
        if (district == null || district.isBlank()) district = "Unknown District";
        if (state == null || state.isBlank()) state = "Unknown State";

        // 9. Create user
        User user = new User();
        user.setUniqueUserId(uniqueUserId);
        user.setUsername(finalUsername);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (isPhoneSignup) {
            user.setPhoneNumber(request.getPhoneNumber());
            user.setPhoneVerified(true);
        } else {
            // Need a dummy phone if not provided but we don't have it nullable, let's see. 
            // Better set it from request if available.
            user.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : uniqueUserId.substring(0, 10));
        }
        if (isEmailSignup) {
            user.setEmail(request.getEmail());
            user.setEmailVerified(true);
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPincode(request.getPincode());
        user.setBlock(block);
        user.setDistrict(district);
        user.setState(state);
        user.setAccountStatus(AccountStatus.PENDING_VERIFICATION);

        // Add the role entity
        user.addRole(role);

        System.out.println(user.getDateOfBirth());

        // 10. Save user
        user = userRepository.save(user);

        // 11. Create profile based on RoleType Enum (Fixes logic error)
        if (roleType == RoleType.ROLE_WORKER) {
            WorkerProfile workerProfile = new WorkerProfile(user);
            workerProfileRepository.save(workerProfile);
        } else if (roleType == RoleType.ROLE_CLIENT) {
            ClientProfile clientProfile = new ClientProfile(user);
            clientProfileRepository.save(clientProfile);
        }

        // 12. Generate JWT token
        String token = jwtTokenProvider.generateToken(user);

        // 13. Return response
        return new AuthResponse(token, new UserResponse(user));
    }

    // Login Step 1: Validate credentials and send OTP
    @Transactional
    public void loginStep1(LoginRequest request) {
        User user = findUserByUsernameOrPhone(request.getUsernameOrPhone());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid credentials");
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new CustomException("Account is " + user.getAccountStatus().toString().toLowerCase());
        }

        otpService.generateAndSendOtp(user.getPhoneNumber(), user.getEmail(), "LOGIN");
    }

    // Login Step 2: Verify OTP and return token
    @Transactional
    public AuthResponse loginStep2(String phoneNumber, String otp) {
        boolean otpValid = otpService.verifyOtp(phoneNumber, otp, "LOGIN");
        if (!otpValid) {
            throw new CustomException("Invalid or expired OTP");
        }

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CustomException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);

        return new AuthResponse(token, new UserResponse(user));
    }

    @Transactional
    public AuthResponse loginStep2Email(String email, String otp) {
        boolean otpValid = otpService.verifyEmailOtp(email, otp, "LOGIN");
        if (!otpValid) {
            throw new CustomException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

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

    // Helper method to find user
    private User findUserByUsernameOrPhone(String usernameOrPhone) {
        if (usernameOrPhone.matches("^[6-9]\\d{9}$")) {
            return userRepository.findByPhoneNumber(usernameOrPhone)
                    .orElseThrow(() -> new CustomException("User not found"));
        } else if (usernameOrPhone.contains("@")) {
            return userRepository.findByEmail(usernameOrPhone)
                    .orElseThrow(() -> new CustomException("User not found"));
        } else {
            return userRepository.findByUsername(usernameOrPhone)
                    .orElseThrow(() -> new CustomException("User not found"));
        }
    }

    @Transactional
    public AuthResponse googleLogin(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    // Specify the CLIENT_ID of the app that accesses the backend, here we accept any audience or check specific one if available
                    // .setAudience(Collections.singletonList("CLIENT_ID"))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String firstName = (String) payload.get("given_name");
                String lastName = (String) payload.get("family_name");

                if (firstName == null) firstName = "Unknown";
                if (lastName == null) lastName = "Unknown";

                Optional<User> userOpt = userRepository.findByEmail(email);
                User user;
                if (userOpt.isPresent()) {
                    user = userOpt.get();
                } else {
                    // Create new user
                    user = new User();
                    String uniqueUserId = uniqueIdGenerator.generateUniqueUserId();
                    user.setUniqueUserId(uniqueUserId);
                    String username = usernameGenerator.generateUsername(firstName, lastName, uniqueUserId);
                    int suffix = 1;
                    String finalUsername = username;
                    while (userRepository.existsByUsername(finalUsername)) {
                        finalUsername = username + suffix;
                        suffix++;
                    }
                    user.setUsername(finalUsername);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);
                    user.setEmailVerified(true);
                    
                    // Dummy defaults for fields that need profile completion
                    user.setPhoneNumber(uniqueUserId.substring(0, 10)); // Required field
                    user.setPassword(passwordEncoder.encode(uniqueUserId)); // Random password
                    user.setDateOfBirth(java.time.LocalDate.now()); // Dummy
                    user.setGender(com.ServeTech.Webapp.entity.enums.GenderType.MALE); // Dummy
                    user.setPincode("000000"); // Dummy
                    user.setBlock("Unknown");
                    user.setDistrict("Unknown");
                    user.setState("Unknown");
                    
                    user.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
                    // Leave role empty, will require profile completion
                    user = userRepository.save(user);
                }

                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);

                String token = jwtTokenProvider.generateToken(user);
                return new AuthResponse(token, new UserResponse(user));

            } else {
                throw new CustomException("Invalid Google ID token");
            }
        } catch (Exception e) {
            throw new CustomException("Failed to verify Google ID token: " + e.getMessage());
        }
    }
}