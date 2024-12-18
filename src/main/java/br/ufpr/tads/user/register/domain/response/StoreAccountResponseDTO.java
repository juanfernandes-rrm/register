package br.ufpr.tads.user.register.domain.response;

import br.ufpr.tads.user.register.domain.model.Store;
import lombok.Data;

import java.util.UUID;

@Data
public class StoreAccountResponseDTO {

    private UUID id;
    private UUID keycloakId;
    private Boolean approved;
    private String name;
    private String email;
    private String cnpj;
    private String urlPhoto;

    public static StoreAccountResponseDTO mapToDTO(Store store) {
        StoreAccountResponseDTO storeAccountResponseDTO = new StoreAccountResponseDTO();
        storeAccountResponseDTO.setId(store.getId());
        storeAccountResponseDTO.setKeycloakId(store.getKeycloakId());
        storeAccountResponseDTO.setApproved(store.getApproved());
        storeAccountResponseDTO.setName(store.getName());
        storeAccountResponseDTO.setEmail(store.getEmail());
        storeAccountResponseDTO.setCnpj(store.getCnpjRoot());
        storeAccountResponseDTO.setUrlPhoto(store.getUrlPhoto());
        return storeAccountResponseDTO;
    }

}
