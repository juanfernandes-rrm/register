package br.ufpr.tads.user.register.domain.response;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
//TODO: criar um generico para User
public class CustomerResponseDTO {

    private UUID id;
    private UUID keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String urlPhoto;

}