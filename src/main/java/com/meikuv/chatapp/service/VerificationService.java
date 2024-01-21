package com.meikuv.chatapp.service;

import com.meikuv.chatapp.controller.response.AuthenticationResponse;
import com.meikuv.chatapp.controller.response.ResponseType;
import com.meikuv.chatapp.model.VerificationModel;
import com.meikuv.chatapp.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerificationService {
    private final UserService userService;
    private final VerificationRepository verificationRepository;

    public Optional<VerificationModel> getVerificationByCodeAndEmail(String code, String email) {
        return verificationRepository.findByCodeAndEmail(code, email);
    }

    @Transactional
    public AuthenticationResponse verifyAccount(String code, String email) {
        VerificationModel verification = getVerificationByCodeAndEmail(code, email)
                .orElseThrow(() ->
                        new IllegalStateException("Verification code not found"));

        if (verification.getConfirmedAt() != null) {
            return AuthenticationResponse.builder()
                    .responseType(ResponseType.FAILED)
                    .message("The account has already been confirmed")
                    .build();
        }

        LocalDateTime expiredAt = verification.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            return AuthenticationResponse.builder()
                    .responseType(ResponseType.FAILED)
                    .message("The verification code has expired")
                    .build();
        }

        verification.setConfirmedAt(LocalDateTime.now());
        verificationRepository.save(verification);

        userService.enableUserAccount(email);

        return AuthenticationResponse.builder()
                .responseType(ResponseType.SUCCESS)
                .message("Verified successfully")
                .build();
    }
}
