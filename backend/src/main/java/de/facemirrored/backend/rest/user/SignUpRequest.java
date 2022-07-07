package de.facemirrored.backend.rest.user;

import lombok.Builder;

@Builder
public record SignUpRequest(String username,
                            String email,
                            String password) {

    @Builder
    public SignUpRequest {
    }
}
