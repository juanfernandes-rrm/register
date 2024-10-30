package br.ufpr.tads.user.register.domain.request;

import lombok.Data;

@Data
//TODO: adicionar validação de campos
public class UserRegistrationRequestDTO {
    private String username;
    private String email;
    private String password;
}
