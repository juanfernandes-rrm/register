package br.ufpr.tads.user.register.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TotalRegisteredUsersResponseDTO {

    private long customers;
    private long stores;

}
