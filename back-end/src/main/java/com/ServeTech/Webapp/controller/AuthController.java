package com.ServeTech.Webapp.controller;

import com.ServeTech.Webapp.dto.request.*;
import com.ServeTech.Webapp.dto.response.ApiResponse;
import com.ServeTech.Webapp.dto.response.AuthResponse;
import com.ServeTech.Webapp.service.AuthService;
import com.ServeTech.Webapp.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Authentication", description = "Authentication APIs for signup, login, OTP verification, and password reset")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/send-otp")
    @Operation(
            summary = "Send OTP to phone number",
            description = "Sends a 6-digit OTP to the provided phone number for verification. Valid for 10 minutes."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid phone number")
    })
    public ResponseEntity<ApiResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        String otp = otpService.generateAndSendOtp(request.getPhoneNumber(), request.getPurpose());

        ApiResponse response = new ApiResponse(
                true,
                "OTP sent successfully to " + request.getPhoneNumber()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    @Operation(
            summary = "Verify OTP",
            description = "Verifies the OTP sent to the phone number"
    )
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        boolean isValid = otpService.verifyOtp(
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

    @PostMapping("/signup")
    @Operation(
            summary = "Register new user",
            description = "Register a new user (Worker or Client) with OTP verification. Creates user account and generates JWT token."
    )
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse authResponse = authService.signup(request);

        ApiResponse response = new ApiResponse(
                true,
                "Registration successful",
                authResponse
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticate user with username/phone and password. Returns JWT token on success."
    )
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        ApiResponse response = new ApiResponse(
                true,
                "Login successful",
                authResponse
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Forgot password - Send OTP",
            description = "Sends OTP to phone number for password reset"
    )
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody SendOtpRequest request) {
        request.setPurpose("PASSWORD_RESET");
        otpService.generateAndSendOtp(request.getPhoneNumber(), request.getPurpose());

        ApiResponse response = new ApiResponse(
                true,
                "OTP sent to your phone number for password reset"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password",
            description = "Reset user password with OTP verification"
    )
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);

        ApiResponse response = new ApiResponse(
                true,
                "Password reset successful"
        );

        return ResponseEntity.ok(response);
    }
}