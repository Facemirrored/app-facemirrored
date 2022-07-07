package de.facemirrored.backend.rest.user;

import java.util.List;

import lombok.Builder;

public record SignInResponse(String token,
                             String username,
                             String email,
                             List<String> roleList) {

    @Builder
    public SignInResponse {
    }
}
