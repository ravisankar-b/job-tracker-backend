package com.ravi.job_tracker_backend.service;

import com.ravi.job_tracker_backend.dto.requestdto.SigninRequestDto;
import com.ravi.job_tracker_backend.dto.responsedto.AuthResponseDto;
import com.ravi.job_tracker_backend.dto.requestdto.SignupRequestDto;
import com.ravi.job_tracker_backend.entity.User;
import com.ravi.job_tracker_backend.exception.JobTrackerCustomException;
import com.ravi.job_tracker_backend.repository.RefreshTokenRepository;
import com.ravi.job_tracker_backend.repository.UserRepository;
import com.ravi.job_tracker_backend.security.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    public AuthResponseDto signup(SignupRequestDto signupRequestDto, HttpServletResponse response) {
        // check if email already exists or not
        if(userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new JobTrackerCustomException("Email Already Exist!", HttpStatus.CONFLICT);
        }
        // convert request dto into user entity and insert into db
        User user = new User();
        user.setUsername(signupRequestDto.getUsername());
        user.setEmail(signupRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        User savedUser = userRepository.save(user);
        // Create refresh token and set as cookie
        String refreshToken = refreshTokenService.createRefreshToken(savedUser).getToken();
        setRefreshTokenCookie(response, refreshToken);
        // convert user into response dto and return it
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(jwtUtil.generateToken(savedUser.getEmail()));
        authResponseDto.setUsername(savedUser.getUsername());
        authResponseDto.setEmail(savedUser.getEmail());
        return authResponseDto;
    }

    public AuthResponseDto signin(SigninRequestDto signinRequestDto, HttpServletResponse response) {
        // check if email is there in db or no
        User user = userRepository.findByEmail(signinRequestDto.getEmail()).
                orElseThrow(()-> new JobTrackerCustomException("User not found!", HttpStatus.NOT_FOUND));
        // check password
        if(!passwordEncoder.matches(signinRequestDto.getPassword(), user.getPassword())) {
            throw new JobTrackerCustomException("Invalid Password", HttpStatus.UNAUTHORIZED);
        }
        // send new refresh token every login
        refreshTokenService.deleteRefreshToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
        setRefreshTokenCookie(response, refreshToken);
        // return response dto and send jwt token
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(jwtUtil.generateToken(user.getEmail()));
        authResponseDto.setUsername(user.getUsername());
        authResponseDto.setEmail(user.getEmail());
        return authResponseDto;
    }

    // To logout
    public void logout(String email, HttpServletResponse response)
    {
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new JobTrackerCustomException("User not found!", HttpStatus.NOT_FOUND));
        refreshTokenService.deleteRefreshToken(user);
        clearRefreshTokenCookie(response);
    }

    // helper methods to set cookie
    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(true)
                .path("/api/auth")
                .maxAge(Duration.ofMillis(refreshExpiration))
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
