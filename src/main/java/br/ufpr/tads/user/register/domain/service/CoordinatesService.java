package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.client.NominatimClient;
import br.ufpr.tads.user.register.domain.model.Address;
import br.ufpr.tads.user.register.domain.response.CoordinatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CoordinatesService {

    private static final int MAX_RETRIES = 5;
    private final Map<String, CoordinatesDTO> coordinatesCache = new ConcurrentHashMap<>();
    private int requestFailures = 0;

    @Autowired
    private NominatimClient nominatimClient;

    public CoordinatesDTO getCoordinates(Address address) {
        String addressKey = createAddressKey(address);

        if (coordinatesCache.containsKey(addressKey)) {
            return coordinatesCache.get(addressKey);
        }

        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                CoordinatesDTO coordinates = nominatimClient.getCoordinatesFromAddress(address);
                coordinatesCache.put(addressKey, coordinates);
                requestFailures = 0;
                return coordinates;
            } catch (Exception e) {
                requestFailures++;
                if (i == MAX_RETRIES - 1) {
                    throw new RuntimeException("Falha ao obter coordenadas após várias tentativas: " + e.getMessage(), e);
                }
            }
        }

        throw new RuntimeException("Erro inesperado ao obter coordenadas.");
    }

    private String createAddressKey(Address address) {
        return address.getStreet() + "," + address.getCity() + "," + address.getState();
    }

    public boolean hasReachedRequestLimit() {
        return requestFailures >= MAX_RETRIES;
    }
}


