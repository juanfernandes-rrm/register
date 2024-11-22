package br.ufpr.tads.user.register.domain.client;

import br.ufpr.tads.user.register.domain.model.Address;
import br.ufpr.tads.user.register.domain.response.ViaCepResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ViaCepClient {

    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/";

    @Autowired
    private RestTemplate restTemplate;

    public Address getAddressFromCep(String cep) {
        String url = VIA_CEP_URL + cep + "/json/";
        try {
            ResponseEntity<ViaCepResponseDTO> response = restTemplate.getForEntity(url, ViaCepResponseDTO.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ViaCepResponseDTO viaCepResponseDTO = response.getBody();

                Address address = new Address();
                address.setStreet(viaCepResponseDTO.getLogradouro());
                address.setNeighborhood(viaCepResponseDTO.getBairro());
                address.setCity(viaCepResponseDTO.getLocalidade());
                address.setState(viaCepResponseDTO.getUf());

                return address;
            } else {
                throw new RuntimeException("Endereço não encontrado para o CEP: " + cep);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar endereço para o CEP: " + cep, e);
        }
    }
}
