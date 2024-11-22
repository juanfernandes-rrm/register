package br.ufpr.tads.user.register.domain.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StoreAccountRequestDTO extends UserRegistrationRequestDTO{

    @NotEmpty
    private String name;

    @NotEmpty
    @Size(min = 14, max = 14)
    private String cnpj;

}
