package com.meikuv.chatapp.controller.response;

import lombok.Getter;

@Getter
public enum ResponseType {
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    ERROR("ERROR");

    private String responseType;

    ResponseType(String responseType) {
        this.responseType = responseType;
    }
}