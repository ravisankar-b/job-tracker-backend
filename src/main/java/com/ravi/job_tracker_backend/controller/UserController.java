package com.ravi.job_tracker_backend.controller;

import com.ravi.job_tracker_backend.dto.requestdto.SigninRequestDto;
import com.ravi.job_tracker_backend.dto.requestdto.SignupRequestDto;
import com.ravi.job_tracker_backend.dto.responsedto.AuthResponseDto;
import com.ravi.job_tracker_backend.exception.JobTrackerCustomException;
import com.ravi.job_tracker_backend.service.RefreshTokenService;
import com.ravi.job_tracker_backend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<AuthResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto,
                                                  HttpServletResponse response)
    {
        return ResponseEntity.status(201).body(userService.signup(signupRequestDto, response));
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDto> signin(@RequestBody SigninRequestDto signinRequestDto,
                                                  HttpServletResponse response)
    {
        return ResponseEntity.ok(userService.signin(signinRequestDto, response));
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@CookieValue(name = "refreshToken",
                                                   required = false) String refreshToken){
        if(refreshToken == null){
            throw new JobTrackerCustomException("Refresh token missing", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(refreshTokenService.verifyRefreshToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal String email, HttpServletResponse response)
    {
        //String email = SecurityContextHolder.getContext()
            //            .getAuthentication().getName();
        userService.logout(email, response);
        return ResponseEntity.ok("Logout Sucessfully");
    }
}
