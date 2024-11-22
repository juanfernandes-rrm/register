package br.ufpr.tads.user.register.domain.client;

import br.ufpr.tads.user.register.domain.model.Address;
import br.ufpr.tads.user.register.domain.response.CoordinatesDTO;
import br.ufpr.tads.user.register.domain.response.NominatimResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class NominatimClient {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    @Autowired
    private RestTemplate restTemplate;

    //TODO: Isso deveria ser um cache distribuído
    private final Map<String, CoordinatesDTO> coordinatesCache = new ConcurrentHashMap<>();

    public CoordinatesDTO getCoordinatesFromAddress(Address address) {
        String addressKey = address.getStreet() + "," + address.getCity() + "," + address.getState();

        if (coordinatesCache.containsKey(addressKey)) {
            return coordinatesCache.get(addressKey);
        }

        String addressQuery = addressKey + ",+Brazil";
        String url = NOMINATIM_URL + "?q=" + addressQuery + "&format=json&limit=1";

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "nota-social-app (juanfernandesrrm@gmail.com)");
        headers.add("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<NominatimResponse[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, NominatimResponse[].class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                NominatimResponse[] body = response.getBody();
                CoordinatesDTO coordinates = new CoordinatesDTO(body[0].getLat(), body[0].getLon());

                coordinatesCache.put(addressKey, coordinates);

                return coordinates;
            } else {
                log.error("Resposta vazia ou inválida da API Nominatim.");
                throw new RuntimeException("Resposta vazia ou inválida da API Nominatim.");
            }
        } catch (Exception e) {
            log.error("Erro ao chamar a API Nominatim:", e);
            throw new RuntimeException("Erro ao chamar a API Nominatim: " + e.getMessage(), e);
        }
    }

}
