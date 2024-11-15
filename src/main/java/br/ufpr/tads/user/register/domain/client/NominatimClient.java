package br.ufpr.tads.user.register.domain.client;

import br.ufpr.tads.user.register.domain.model.Address;
import br.ufpr.tads.user.register.domain.response.CoordinatesDTO;
import br.ufpr.tads.user.register.domain.response.NominatimResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NominatimClient {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";

    @Autowired
    private RestTemplate restTemplate;

    public CoordinatesDTO getCoordinatesFromAddress(Address address) {
        String addressQuery = address.getStreet() + ",+" + address.getCity() + ",+" + address.getState() + ",+Brazil";
        String url = NOMINATIM_URL + "?q=" + addressQuery + "&format=json&limit=1";

        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "nota-social-app (juanfernandesrrm@gmail.com)");
        headers.add("Accept", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<NominatimResponse[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, NominatimResponse[].class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                NominatimResponse[] body = response.getBody();
                return new CoordinatesDTO(body[0].getLat(), body[0].getLon());
            } else {
                throw new RuntimeException("Resposta vazia ou inválida da API Nominatim.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao chamar a API Nominatim: " + e.getMessage(), e);
        }
    }

}
