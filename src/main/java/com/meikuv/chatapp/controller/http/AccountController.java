package com.meikuv.chatapp.controller.http;

import com.meikuv.chatapp.controller.request.ChangePasswordRequest;
import com.meikuv.chatapp.controller.request.VerificationRequest;
import com.meikuv.chatapp.controller.response.AuthenticationResponse;
import com.meikuv.chatapp.controller.response.ResponseType;
import com.meikuv.chatapp.service.UserService;
import com.meikuv.chatapp.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/account")
public class AccountController {
    private final UserService userService;
    private final VerificationService verificationService;

    @PatchMapping("/change-password")
    public ResponseEntity<ResponseType> changePassword(@RequestBody ChangePasswordRequest request, Principal principal) {
        userService.changeUserPassword(request, principal);
        return ResponseEntity.ok(ResponseType.SUCCESS);
    }

    @GetMapping("/verification")
    public ResponseEntity<AuthenticationResponse> verifyWithCode(@RequestBody VerificationRequest request) {
        return ResponseEntity.ok(verificationService
                .verifyAccount(request.getVerificationCode(), request.getEmail()));
    }
}
