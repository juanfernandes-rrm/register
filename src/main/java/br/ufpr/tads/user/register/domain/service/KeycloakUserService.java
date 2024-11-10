package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.exception.UserCreationException;
import br.ufpr.tads.user.register.domain.request.CustomerRequestDTO;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

@Slf4j
@Service
public class KeycloakUserService {

    @Autowired
    private Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Autowired
    private KeycloakRoleService keycloakRoleService;

    public String registerUser(CustomerRequestDTO userDTO){
        CredentialRepresentation credential = createPasswordCredentials(userDTO.getPassword());

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userDTO.getFirstName());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setCredentials(Collections.singletonList(credential));
        user.setEmailVerified(false);

        keycloak.tokenManager().getAccessTokenString();

        try (Response response = keycloak.realm(realm).users().create(user)) {
            if (Objects.equals(201, response.getStatus())) {
                String userId = extractUserIdFromLocation(response);
                keycloakRoleService.assignRole(getUsersResource().get(userId), "CUSTOMER");
                log.info("User successful created: " + userId);
                return userId;
            } else {
                String errorMessage = response.readEntity(String.class);
                log.error("Error creating user: " + errorMessage);
                throw new UserCreationException("Error creating user: " + errorMessage);
            }
        }
    }

    public UserRepresentation getUserById(String userId) {
        return  getUsersResource().get(userId).toRepresentation();
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    public void deleteUserById(String userId) {
        getUsersResource().delete(userId);
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private String extractUserIdFromLocation(Response response) {
        String locationHeader = response.getHeaderString("Location");
        return locationHeader != null ? locationHeader.substring(locationHeader.lastIndexOf('/') + 1) : null;
    }

    public String getServiceAccountToken() {
        return keycloak.tokenManager().getAccessTokenString();
    }

}
