package com.meikuv.chatapp.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class SignInRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
