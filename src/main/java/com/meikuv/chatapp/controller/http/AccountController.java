package com.meikuv.chatapp.controller.http;

import com.meikuv.chatapp.controller.request.ChangePasswordRequest;
import com.meikuv.chatapp.controller.request.VerificationRequest;
import com.meikuv.chatapp.controller.response.AuthenticationResponse;
import com.meikuv.chatapp.controller.response.ConnectedUserResponse;
import com.meikuv.chatapp.controller.response.MessageResponse;
import com.meikuv.chatapp.controller.response.ResponseType;
import com.meikuv.chatapp.model.UserModel;
import com.meikuv.chatapp.service.UserService;
import com.meikuv.chatapp.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

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

    @GetMapping("/me")
    public ResponseEntity<?> changePassword(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MessageResponse.builder()
                            .message("Unauthorized access")
                            .build());
        }

        UserModel user = userService.findByUsername(principal.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(MessageResponse.builder()
                            .message("User not found")
                            .build());
        }

        return ResponseEntity.ok(ConnectedUserResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .build());
    }
}
