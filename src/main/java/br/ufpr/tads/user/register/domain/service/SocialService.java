package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.dto.request.GetUserProfileDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Service
public class SocialService {

    private static String CREATE_PROFILE = "/profile";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${social.service.url}")
    private String socialServiceUrl;

    //TODO: Adicionar circuit breaker
    public boolean createProfile(UUID keycloakId, String token) {
        if(keycloakId == null || token == null){
            log.error("KeycloakId or token is null");
            return false;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<UUID> entity = new HttpEntity<>(keycloakId, headers);

        ResponseEntity<GetUserProfileDTO> response = restTemplate.exchange(
                socialServiceUrl + CREATE_PROFILE,
                HttpMethod.POST,
                entity,
                GetUserProfileDTO.class
        );

        boolean isUserCreatedInSocial = response.getStatusCode().is2xxSuccessful();
        if(isUserCreatedInSocial){
            log.info("User created in social service");
        } else {
            log.error("User not created in social service");
        }
        return isUserCreatedInSocial;
    }

}
