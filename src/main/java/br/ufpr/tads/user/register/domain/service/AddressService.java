package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.client.NominatimClient;
import br.ufpr.tads.user.register.domain.client.ViaCepClient;
import br.ufpr.tads.user.register.domain.model.Address;
import br.ufpr.tads.user.register.domain.response.CoordinatesDTO;
import br.ufpr.tads.user.register.domain.utils.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    @Autowired
    private ViaCepClient viaCepClient;

    @Autowired
    private NominatimClient nominatimClient;

    public Address getAddressByCep(String cep) {
        return viaCepClient.getAddressFromCep(cep);
    }

    public double calculateDistanceBetweenAddresses(Address address1, Address address2) {
        CoordinatesDTO userCoordinatesDTO = nominatimClient.getCoordinatesFromAddress(address1);
        CoordinatesDTO branchCoordinatesDTO = nominatimClient.getCoordinatesFromAddress(address2);
        return DistanceCalculator.calculateDistance(userCoordinatesDTO.getLat(), userCoordinatesDTO.getLon(),
                branchCoordinatesDTO.getLat(), branchCoordinatesDTO.getLon());
    }

}
