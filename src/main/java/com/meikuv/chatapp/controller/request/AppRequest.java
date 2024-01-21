package com.meikuv.chatapp.controller.request;

import lombok.*;

@Getter
@Setter
public class AppRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
}
