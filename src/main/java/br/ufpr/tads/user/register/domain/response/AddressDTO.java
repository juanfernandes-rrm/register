package br.ufpr.tads.user.register.domain.response;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private String street;
    private String number;
    private String neighborhood;
    private String city;
    private String state;

}
