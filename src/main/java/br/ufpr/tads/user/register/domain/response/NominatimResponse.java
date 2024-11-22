package br.ufpr.tads.user.register.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimResponse {
    private String lat;
    private String lon;
}