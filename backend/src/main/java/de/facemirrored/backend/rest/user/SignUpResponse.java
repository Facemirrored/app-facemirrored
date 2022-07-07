package de.facemirrored.backend.rest.user;

import java.util.List;

import lombok.Builder;

public record SignUpResponse(SignUpStatus signUpStatus,
                             String username,
                             String email, List<String> roleList) {

    @Builder
    public SignUpResponse {
    }
}
