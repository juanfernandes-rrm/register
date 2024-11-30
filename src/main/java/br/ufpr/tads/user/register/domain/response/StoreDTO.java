package br.ufpr.tads.user.register.domain.response;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Trocar para BranchDTO -> essa é a loja retornada pelo receiptscan
public class StoreDTO {
    private UUID id;
    private String name;
    private AddressDTO address;
    private String cnpj;


    public StoreDTO(String id, String name, AddressDTO address, String cnpj) {
        this.id = UUID.fromString(id);
        this.name = name;
        this.address = address;
        this.cnpj = cnpj;
    }
}
