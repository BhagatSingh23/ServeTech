package com.ServeTech.Webapp.controller;

import com.ServeTech.Webapp.dto.request.*;
import com.ServeTech.Webapp.dto.response.ApiResponse;
import com.ServeTech.Webapp.dto.response.AuthResponse;
import com.ServeTech.Webapp.service.AuthService;
import com.ServeTech.Webapp.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {


    private final AuthService authService;

    private final OtpService otpService;

    public AuthController(AuthService authService, OtpService otpService) {
        this.authService = authService;
        this.otpService = otpService;
    }

    // Will use to send OTP to user's phone number and other authentication purposes later on
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        otpService.generateAndSendOtp(request.getPhoneNumber(), request.getPurpose());

        ApiResponse response = new ApiResponse(
                true,
                "OTP sent successfully to " + request.getPhoneNumber()
        );

        return ResponseEntity.ok(response);
    }

    // verify OTP sent to user's phone number
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        otpService.verifyOtp(
                request.getPhoneNumber(),
                request.getOtp(),
                request.getPurpose()
        );

        ApiResponse response = new ApiResponse(
                true,
                "OTP verified successfully"
        );

        return ResponseEntity.ok(response);
    }

    // Signup user
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse authResponse = authService.signup(request);

        ApiResponse response = new ApiResponse(
                true,
                "Registration successful",
                authResponse
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Login user
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        ApiResponse response = new ApiResponse(
                true,
                "Login successful",
                authResponse
        );

        return ResponseEntity.ok(response);
    }

    // If forgot password, send OTP to user's phone number
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody SendOtpRequest request) {
        request.setPurpose("PASSWORD_RESET");
        otpService.generateAndSendOtp(request.getPhoneNumber(), request.getPurpose());

        ApiResponse response = new ApiResponse(
                true,
                "OTP sent to your phone number for password reset"
        );

        return ResponseEntity.ok(response);
    }

    // Method to reset password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);

        ApiResponse response = new ApiResponse(
                true,
                "Password reset successful"
        );

        return ResponseEntity.ok(response);
    }
}
