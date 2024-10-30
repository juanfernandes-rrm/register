package br.ufpr.tads.user.register.domain.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerRequestDTO {

    @NotBlank(message = "name is required")
    private String username;

    @NotBlank(message = "e-mail is required")
    @Email(message = "Invalid e-mail")
    private String email;

    @NotBlank(message = "password is required")
    private String password;

}