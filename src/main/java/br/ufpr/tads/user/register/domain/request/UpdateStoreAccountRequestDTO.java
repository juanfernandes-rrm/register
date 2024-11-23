package br.ufpr.tads.user.register.domain.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoreAccountRequestDTO {
    @NotBlank(message = "firstname is required")
    private String name;

    @NotBlank(message = "e-mail is required")
    @Email(message = "Invalid e-mail")
    private String email;

    private String urlPhoto;
}
