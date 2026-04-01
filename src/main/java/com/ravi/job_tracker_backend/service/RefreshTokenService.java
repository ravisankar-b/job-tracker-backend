package com.ravi.job_tracker_backend.service;

import com.ravi.job_tracker_backend.dto.responsedto.AuthResponseDto;
import com.ravi.job_tracker_backend.entity.RefreshToken;
import com.ravi.job_tracker_backend.entity.User;
import com.ravi.job_tracker_backend.exception.JobTrackerCustomException;
import com.ravi.job_tracker_backend.repository.RefreshTokenRepository;
import com.ravi.job_tracker_backend.repository.UserRepository;
import com.ravi.job_tracker_backend.security.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @PersistenceContext
    private EntityManager entityManager;

    // To create Refresh Token during signup and signin
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshExpiration));
        return refreshTokenRepository.save(refreshToken);
    }

    // To verify refresh token and pass new access token
    public AuthResponseDto verifyRefreshToken(String refreshTokenRequest)
    {
        // verify token present or not
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenRequest)
                .orElseThrow(()-> new JobTrackerCustomException("Error fetching data, Please Login Again",
                        HttpStatus.BAD_REQUEST));
        // verify token expired or not
        if(refreshToken.getExpiryDate().compareTo(Instant.now())<0)
        {
            refreshTokenRepository.delete(refreshToken);
            throw new JobTrackerCustomException("Error fetching data, Please Login Again",
                    HttpStatus.UNAUTHORIZED);
        }
        // create new access token
        String token = jwtUtil.generateToken(refreshToken.getUser().getEmail());
        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(token);
        authResponseDto.setUsername(refreshToken.getUser().getUsername());
        authResponseDto.setEmail(refreshToken.getUser().getEmail());
        return authResponseDto;
    }
    // Delete Refresh Token
    @Transactional
    public void deleteRefreshToken(User user){
        refreshTokenRepository.deleteByUser(user);
    }
}
