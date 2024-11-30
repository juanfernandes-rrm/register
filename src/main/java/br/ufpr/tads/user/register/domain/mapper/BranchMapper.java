package br.ufpr.tads.user.register.domain.mapper;

import br.ufpr.tads.user.register.domain.model.Address;
import br.ufpr.tads.user.register.domain.model.Branch;
import br.ufpr.tads.user.register.domain.response.AddressDTO;
import br.ufpr.tads.user.register.domain.response.BranchDTO;
import br.ufpr.tads.user.register.domain.response.StoreDTO;

public class BranchMapper {

    public static BranchDTO convertToDTO(Branch branch, double distance) {
        BranchDTO branchDTO = new BranchDTO();
        branchDTO.setId(branch.getId());
        branchDTO.setCorrelationId(branch.getCorrelationId());

        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setId(branch.getStore().getId());
        storeDTO.setName(branch.getStore().getName());
        storeDTO.setCnpj(branch.getStore().getCnpjRoot());

        Address address = branch.getAddress();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet(address.getStreet());
        addressDTO.setNumber(address.getNumber());
        addressDTO.setNeighborhood(address.getNeighborhood());
        addressDTO.setCity(address.getCity());
        addressDTO.setState(address.getState());
        storeDTO.setAddress(addressDTO);

        branchDTO.setStore(storeDTO);
        branchDTO.setDistance(distance);
        return branchDTO;
    }

}
