package br.ufpr.tads.user.register.domain.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustomerAccountRequestDTO extends UserRegistrationRequestDTO{

    @NotBlank(message = "name is required")
    private String firstName;

    @NotBlank(message = "name is required")
    private String lastName;

}