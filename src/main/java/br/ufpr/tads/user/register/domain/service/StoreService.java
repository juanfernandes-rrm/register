package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.model.Address;
import br.ufpr.tads.user.register.domain.model.Branch;
import br.ufpr.tads.user.register.domain.model.Store;
import br.ufpr.tads.user.register.domain.repository.BranchRepository;
import br.ufpr.tads.user.register.domain.repository.StoreRepository;
import br.ufpr.tads.user.register.domain.response.AddressDTO;
import br.ufpr.tads.user.register.domain.response.BranchDTO;
import br.ufpr.tads.user.register.domain.response.StoreDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public BranchDTO getStoreBranch(UUID correlationId) {
        Optional<Branch> branchOptional = branchRepository.findByCorrelationId(correlationId);
        return branchOptional.map(this::mapBranchToDTO).orElse(null);
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

    private BranchDTO mapBranchToDTO(Branch branch) {
        Address address = branch.getAddress();
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet(address.getStreet());
        addressDTO.setNumber(address.getNumber());
        addressDTO.setNeighborhood(address.getNeighborhood());
        addressDTO.setCity(address.getCity());
        addressDTO.setState(address.getState());

        Store store = branch.getStore();
        StoreDTO storeDTO = new StoreDTO();
        storeDTO.setId(store.getId());
        storeDTO.setCnpj(store.getCNPJ());
        storeDTO.setName(store.getName());
        storeDTO.setAddress(addressDTO);

        BranchDTO branchDTO = new BranchDTO();
        branchDTO.setId(branch.getId());
        branchDTO.setStore(storeDTO);
        branchDTO.setCorrelationId(branch.getCorrelationId());

        return branchDTO;
    }

}
