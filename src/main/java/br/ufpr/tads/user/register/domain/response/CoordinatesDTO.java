package br.ufpr.tads.user.register.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoordinatesDTO {
    private double lat;
    private double lon;

    public CoordinatesDTO(String lat, String lon) {
        this.lat = Double.parseDouble(lat);
        this.lon = Double.parseDouble(lon);
    }
}
