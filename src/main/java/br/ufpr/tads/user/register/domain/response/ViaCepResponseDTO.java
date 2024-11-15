package br.ufpr.tads.user.register.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ViaCepResponseDTO {
    private String logradouro;
    private String bairro;
    private String localidade;
    private String uf;
    private String cep;
}
