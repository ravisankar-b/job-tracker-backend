package com.ravi.job_tracker_backend.service;

import com.ravi.job_tracker_backend.dto.requestdto.SigninRequestDto;
import com.ravi.job_tracker_backend.dto.responsedto.AuthResponseDto;
import com.ravi.job_tracker_backend.dto.requestdto.SignupRequestDto;
import com.ravi.job_tracker_backend.entity.User;
import com.ravi.job_tracker_backend.exception.JobTrackerCustomException;
import com.ravi.job_tracker_backend.repository.RefreshTokenRepository;
import com.ravi.job_tracker_backend.repository.UserRepository;
import com.ravi.job_tracker_backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public AuthResponseDto signup(SignupRequestDto signupRequestDto) {
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
        // convert user into response dto and return it
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(jwtUtil.generateToken(savedUser.getEmail()));
        authResponseDto.setRefreshToken(refreshTokenService.createRefreshToken(savedUser).getToken());
        authResponseDto.setUsername(savedUser.getUsername());
        authResponseDto.setEmail(savedUser.getEmail());
        return authResponseDto;
    }

    public AuthResponseDto signin(SigninRequestDto signinRequestDto) {
        // check if email is there in db or no
        User user = userRepository.findByEmail(signinRequestDto.getEmail()).
                orElseThrow(()-> new JobTrackerCustomException("User not found!", HttpStatus.NOT_FOUND));
        // check password
        if(!passwordEncoder.matches(signinRequestDto.getPassword(), user.getPassword())) {
            throw new JobTrackerCustomException("Invalid Password", HttpStatus.UNAUTHORIZED);
        }

        // return response dto and send jwt token
        AuthResponseDto authResponseDto = new AuthResponseDto();
        refreshTokenService.deleteRefreshToken(user);
        authResponseDto.setAccessToken(jwtUtil.generateToken(user.getEmail()));
        authResponseDto.setRefreshToken(refreshTokenService.createRefreshToken(user).getToken());
        authResponseDto.setUsername(user.getUsername());
        authResponseDto.setEmail(user.getEmail());
        return authResponseDto;
    }

    // To logout
    public void logout(String email)
    {
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new JobTrackerCustomException("User not found!", HttpStatus.NOT_FOUND));
        refreshTokenService.deleteRefreshToken(user);
    }
}
