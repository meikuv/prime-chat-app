package com.meikuv.chatapp.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.meikuv.chatapp.config.jwt.JwtUtils;
import com.meikuv.chatapp.controller.response.AuthenticationResponse;
import com.meikuv.chatapp.controller.response.MessageResponse;
import com.meikuv.chatapp.controller.response.ResponseType;
import com.meikuv.chatapp.exception.RefreshTokenException;
import com.meikuv.chatapp.model.RefreshTokenModel;
import com.meikuv.chatapp.repository.RefreshTokenRepository;
import com.meikuv.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${primechat.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public Optional<RefreshTokenModel> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshTokenModel createRefreshToken(Long userId) {
        RefreshTokenModel refreshToken = RefreshTokenModel.builder()
                .user(userRepository.findById(userId).get())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .token(UUID.randomUUID().toString())
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshTokenModel verifyExpiration(RefreshTokenModel token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    public ResponseEntity<?> createAccessToken(String refreshToken) {
        if ((refreshToken != null) && (!refreshToken.isEmpty())) {
            return findByToken(refreshToken)
                    .map(this::verifyExpiration)
                    .map(RefreshTokenModel::getUser)
                    .map(userModel -> {
                        String accessToken = jwtUtils.generateTokenFromUsername(userModel.getUsername());

                        return ResponseEntity.ok(AuthenticationResponse.builder()
                                .responseType(ResponseType.SUCCESS)
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .message("Token is refreshed successfully")
                                .build());
                    })
                    .orElseThrow(() -> new RefreshTokenException(refreshToken, "Refresh token is not in database"));

        }

        return ResponseEntity.badRequest().body(
                MessageResponse.builder()
                        .message("Refresh Token is empty!")
                        .build());
    }
}
