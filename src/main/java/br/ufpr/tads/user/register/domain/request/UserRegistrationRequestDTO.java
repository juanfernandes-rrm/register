package br.ufpr.tads.user.register.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRegistrationRequestDTO {

    @NotBlank(message = "e-mail is required")
    @Email(message = "Invalid e-mail")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

    private String urlPhoto;
}
