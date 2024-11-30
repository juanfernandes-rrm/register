package br.ufpr.tads.user.register.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreAccountRequestDTO {
    private String name;

    private String email;

    private String password;

}
