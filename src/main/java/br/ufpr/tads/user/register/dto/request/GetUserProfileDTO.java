package br.ufpr.tads.user.register.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class GetUserProfileDTO {

    private UUID id;
    private UUID keycloakId;
    private String firstName;
    private String lastName;
    private String email;
    private String photo;

}
