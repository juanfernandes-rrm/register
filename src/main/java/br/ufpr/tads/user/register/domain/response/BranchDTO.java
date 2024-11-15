package br.ufpr.tads.user.register.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchDTO {
    private UUID id;
    private UUID correlationId;
    private StoreDTO store;
    private double distance;
}
