package com.meikuv.chatapp.controller.websocket;

import com.meikuv.chatapp.model.UserModel;
import com.meikuv.chatapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @MessageMapping("/user.availability")
    @SendTo("/user/topic")
    public void updateAvailabilityStatus(@Payload String status, String username) {
        userService.updateUserStatus(status, username);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserModel>> findConnectedUser() {
        return ResponseEntity.ok(userService.findConnectedUsers());
    }
}
