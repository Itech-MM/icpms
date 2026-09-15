package org.flexitech.projects.icpms.dto.api.request.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotNull(message = "Authentication methods should not be null.")
    private Integer authMethod;

    private String username;

    private String password;

    private String credential;
}