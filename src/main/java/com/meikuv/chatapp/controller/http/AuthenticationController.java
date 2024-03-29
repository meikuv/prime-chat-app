package com.meikuv.chatapp.controller.http;

import com.meikuv.chatapp.controller.request.RefreshTokenRequest;
import com.meikuv.chatapp.controller.request.SignInRequest;
import com.meikuv.chatapp.controller.request.SignUpRequest;
import com.meikuv.chatapp.controller.response.AuthenticationResponse;
import com.meikuv.chatapp.service.AuthenticationService;
import com.meikuv.chatapp.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authenticationService.register(signUpRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody SignInRequest signInRequest) {
        return authenticationService.login(signInRequest);
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@RequestBody RefreshTokenRequest request) {
        return refreshTokenService.createAccessToken(request.getRefreshToken());
    }
}
