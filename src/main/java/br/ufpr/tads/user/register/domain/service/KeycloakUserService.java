package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.exception.UserCreationException;
import br.ufpr.tads.user.register.domain.request.CustomerAccountRequestDTO;
import br.ufpr.tads.user.register.domain.request.StoreAccountRequestDTO;
import br.ufpr.tads.user.register.domain.request.UserRegistrationRequestDTO;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
public class KeycloakUserService {

    @Autowired
    private Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Autowired
    private KeycloakRoleService keycloakRoleService;

    public UUID registerUser(UserRegistrationRequestDTO userDTO) {
        if (userDTO instanceof CustomerAccountRequestDTO) {
            return registerCustomer((CustomerAccountRequestDTO) userDTO);
        } else if (userDTO instanceof StoreAccountRequestDTO) {
            return registerStore((StoreAccountRequestDTO) userDTO);
        } else {
            throw new UserCreationException("Invalid type of user.");
        }
    }

    public UserRepresentation getUserById(String userId) {
        return  getUsersResource().get(userId).toRepresentation();
    }

    public void deleteUserById(String userId) {
        getUsersResource().delete(userId);
    }

    public void updateUser(String userId, UserRepresentation updatedUser) {
        try {
            UserResource userResource = getUsersResource().get(userId);
            userResource.update(updatedUser);
            log.info("User with ID {} successfully updated.", userId);
        } catch (Exception e) {
            log.error("Failed to update user with ID {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    public String getServiceAccountToken() {
        return keycloak.tokenManager().getAccessTokenString();
    }

    private UUID registerCustomer(CustomerAccountRequestDTO userDTO) {
        CredentialRepresentation credential = createPasswordCredentials(userDTO.getPassword());

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setCredentials(Collections.singletonList(credential));
        user.setEmailVerified(false);

        String accountId = createUserAccount(user);
        keycloakRoleService.assignRole(getUsersResource().get(accountId), "CUSTOMER");
        return UUID.fromString(accountId);
    }

    private UUID registerStore(StoreAccountRequestDTO userDTO) {
        CredentialRepresentation credential = createPasswordCredentials(userDTO.getPassword());

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userDTO.getEmail());
        user.setFirstName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setCredentials(Collections.singletonList(credential));
        user.setEmailVerified(false);

        String accountId = createUserAccount(user);
        keycloakRoleService.assignRole(getUsersResource().get(accountId), "STORE");
        return UUID.fromString(accountId);
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private String createUserAccount(UserRepresentation user) {
        try (Response response = keycloak.realm(realm).users().create(user)) {
            if (response.getStatus() == 201) {
                log.info("User successful created: " + user.getUsername());
                return extractUserIdFromLocation(response);
            } else {
                String errorMessage = response.readEntity(String.class);
                log.info("Error creating user: " + errorMessage);
                throw new UserCreationException(errorMessage);
            }
        }
    }

    private String extractUserIdFromLocation(Response response) {
        String locationHeader = response.getHeaderString("Location");
        return locationHeader != null ? locationHeader.substring(locationHeader.lastIndexOf('/') + 1) : null;
    }

}
