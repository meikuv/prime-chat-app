package com.meikuv.chatapp.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectedUserResponse {
    private String firstName;
    private String lastName;
    private String username;
    private String email;
}
