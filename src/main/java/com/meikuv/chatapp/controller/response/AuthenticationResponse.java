package com.meikuv.chatapp.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private ResponseType responseType;
    private String message;
    private String accessToken;
    private String refreshToken;
}
