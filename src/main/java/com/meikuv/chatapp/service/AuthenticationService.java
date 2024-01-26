package com.meikuv.chatapp.service;

import com.meikuv.chatapp.config.user.UserDetailsImpl;
import com.meikuv.chatapp.config.jwt.JwtUtils;
import com.meikuv.chatapp.controller.request.SignInRequest;
import com.meikuv.chatapp.controller.request.SignUpRequest;
import com.meikuv.chatapp.controller.response.AuthenticationResponse;
import com.meikuv.chatapp.controller.response.ResponseType;
import com.meikuv.chatapp.model.RefreshTokenModel;
import com.meikuv.chatapp.controller.response.StatusType;
import com.meikuv.chatapp.model.UserModel;
import com.meikuv.chatapp.model.VerificationModel;
import com.meikuv.chatapp.repository.UserRepository;
import com.meikuv.chatapp.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;

    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    public AuthenticationResponse register(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return AuthenticationResponse.builder()
                    .responseType(ResponseType.FAILED)
                    .build();
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return AuthenticationResponse.builder()
                    .responseType(ResponseType.FAILED)
                    .build();
        }
        String verificationCode = emailService.generateCode();
        emailService.sendHTMLEmail(signUpRequest.getEmail(), signUpRequest.getUsername(), verificationCode);

        VerificationModel verification = VerificationModel.builder()
                .email(signUpRequest.getEmail())
                .code(verificationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        UserModel user = UserModel.builder()
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .enabled(false)
                .locked(false)
                .build();

        userRepository.save(user);
        verificationRepository.save(verification);

        return AuthenticationResponse.builder()
                .responseType(ResponseType.SUCCESS)
                .build();
    }

    public ResponseEntity<AuthenticationResponse> login(SignInRequest signInRequest) {
        UserModel user = userRepository.findByUsername(signInRequest.getUsername());

        if (user == null) {
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .message("User not found with username: "  + signInRequest.getUsername())
                    .responseType(ResponseType.FAILED)
                    .build());
        }

        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .message("Wrong username or password")
                    .responseType(ResponseType.FAILED)
                    .build());
        }

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        RefreshTokenModel refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        user.setStatusType(StatusType.ONLINE);
        userRepository.save(user);

        return ResponseEntity.ok()
                .body(AuthenticationResponse.builder()
                        .accessToken(jwt)
                        .message("Successfully logged in")
                        .refreshToken(refreshToken.getToken())
                        .responseType(ResponseType.SUCCESS)
                        .build());
    }
}
