package br.ufpr.tads.user.register.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "USER_APP")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "KEYCLOAK_ID", unique = true)
    private UUID keycloakId;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "URL_PHOTO")
    private String urlPhoto;

}
