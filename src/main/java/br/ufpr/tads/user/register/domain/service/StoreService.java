package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.model.Address;
import br.ufpr.tads.user.register.domain.model.Branch;
import br.ufpr.tads.user.register.domain.model.Store;
import br.ufpr.tads.user.register.domain.repository.BranchRepository;
import br.ufpr.tads.user.register.domain.repository.StoreRepository;
import br.ufpr.tads.user.register.domain.response.AddressDTO;
import br.ufpr.tads.user.register.domain.response.StoreDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Transactional
    public Branch createOrUpdateStore(StoreDTO storeDTO) {
        Optional<Branch> existingBranch = branchRepository.findByCorrelationId(storeDTO.getId());

        if (existingBranch.isPresent()) {
            return existingBranch.get();
        }

        Optional<Store> existingStore = storeRepository.findByCNPJ(storeDTO.getCnpj());

        return existingStore.map(store -> addNewBranchToExistingStore(store, storeDTO))
                .orElseGet(() -> createNewStoreWithBranch(storeDTO));
    }

    private Branch addNewBranchToExistingStore(Store store, StoreDTO storeDTO) {
        Branch newBranch = convertToBranch(storeDTO);
        newBranch.setStore(store);

        store.getBranches().add(newBranch);

        storeRepository.save(store);
        return newBranch;
    }

    private Branch createNewStoreWithBranch(StoreDTO storeDTO) {
        Store newStore = new Store();
        newStore.setCNPJ(storeDTO.getCnpj());
        newStore.setName(storeDTO.getName());

        Branch newBranch = convertToBranch(storeDTO);
        newBranch.setStore(newStore);

        newStore.setBranches(List.of(newBranch));

        storeRepository.save(newStore);
        return newBranch;
    }

    private Branch convertToBranch(StoreDTO storeDTO) {
        Branch branch = new Branch();
        AddressDTO addressDTO = storeDTO.getAddress();
        Address address = new Address();
        address.setStreet(addressDTO.getStreet());
        address.setNumber(addressDTO.getNumber());
        address.setNeighborhood(addressDTO.getNeighborhood());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());

        branch.setAddress(address);
        branch.setCorrelationId(storeDTO.getId());
        return branch;
    }

}
