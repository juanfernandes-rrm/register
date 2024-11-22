package br.ufpr.tads.user.register.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
public class KeycloakRoleService {

    @Value("${keycloak.realm}")
    private String realm;

    @Autowired
    private Keycloak keycloak;

    public void assignRole(UserResource userResource, String roleName) {
        try {
            RolesResource rolesResource = getRolesResource();
            RoleRepresentation representation = rolesResource.get(roleName).toRepresentation();
            userResource.roles().realmLevel().add(Collections.singletonList(representation));
        } catch (Exception e) {
            log.error("Erro ao atribuir role: " + e.getMessage(), e);
            throw new RuntimeException("Erro ao atribuir role: " + e.getMessage(), e);
        }
    }

    private RolesResource getRolesResource(){
        return  keycloak.realm(realm).roles();
    }


}
