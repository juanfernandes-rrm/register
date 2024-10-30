package br.ufpr.tads.user.register.domain.response;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class CustomerResponseDTO {

    private UUID id;
    private String username;
    private String email;

}