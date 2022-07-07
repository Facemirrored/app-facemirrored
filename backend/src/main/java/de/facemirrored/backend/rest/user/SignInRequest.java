package de.facemirrored.backend.rest.user;

import lombok.Builder;
import lombok.Getter;

public record SignInRequest(String username,
                            String password,
                            String email) {

    @Builder
    public SignInRequest {
    }
}
