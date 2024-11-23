package br.ufpr.tads.user.register.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateCustomerAccountRequestDTO {
    @NotBlank(message = "firstname is required")
    private String firstName;

    @NotBlank(message = "lastname is required")
    private String lastName;

    @NotBlank(message = "e-mail is required")
    @Email(message = "Invalid e-mail")
    private String email;

    private String urlPhoto;
}
