package com.meikuv.chatapp.service;

import com.meikuv.chatapp.controller.request.ChangePasswordRequest;
import com.meikuv.chatapp.model.UserModel;
import com.meikuv.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void changeUserPassword(ChangePasswordRequest request, Principal connectedUser) {
        UserModel user = (UserModel) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }

        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        if (passwordEncoder.matches(user.getPassword(), request.getNewPassword())) {
            throw new IllegalStateException("Current and new password are the same");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    public Optional<UserModel> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void enableUserAccount(String email) {
        UserModel user = findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
    }
}
