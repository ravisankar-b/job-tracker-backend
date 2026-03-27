package com.ravi.job_tracker_backend.controller;

import com.ravi.job_tracker_backend.dto.requestdto.RefreshTokenRequestDto;
import com.ravi.job_tracker_backend.dto.requestdto.SigninRequestDto;
import com.ravi.job_tracker_backend.dto.requestdto.SignupRequestDto;
import com.ravi.job_tracker_backend.dto.responsedto.AuthResponseDto;
import com.ravi.job_tracker_backend.service.RefreshTokenService;
import com.ravi.job_tracker_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto)
    {
        return ResponseEntity.status(201).body(userService.signup(signupRequestDto));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDto> signin(@RequestBody SigninRequestDto signinRequestDto)
    {
        return ResponseEntity.ok(userService.signin(signinRequestDto));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@RequestBody RefreshTokenRequestDto
                                                               refreshTokenRequestDto){
        return ResponseEntity.ok(refreshTokenService.verifyRefreshToken(refreshTokenRequestDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout()
    {
        String email = SecurityContextHolder.getContext()
                        .getAuthentication().getName();
        userService.logout(email);
        return ResponseEntity.ok("Logout Sucessfully");
    }
}
