package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.model.Address;
import br.ufpr.tads.user.register.domain.model.Branch;
import br.ufpr.tads.user.register.domain.model.Store;
import br.ufpr.tads.user.register.domain.repository.BranchRepository;
import br.ufpr.tads.user.register.domain.repository.StoreRepository;
import br.ufpr.tads.user.register.domain.request.StoreAccountRequestDTO;
import br.ufpr.tads.user.register.domain.response.AddressDTO;
import br.ufpr.tads.user.register.domain.response.BranchDTO;
import br.ufpr.tads.user.register.domain.response.StoreAccountResponseDTO;
import br.ufpr.tads.user.register.domain.response.StoreDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private KeycloakUserService keycloakUserService;

    public StoreAccountResponseDTO registerStore(StoreAccountRequestDTO storeAccountRequestDTO) {
        Optional<Store> optionalStore = storeRepository.findByCNPJ(storeAccountRequestDTO.getCnpj());

        if (optionalStore.isEmpty()) {
            UUID storeAccountId = keycloakUserService.registerUser(storeAccountRequestDTO);
            Store newStoreWithoutBranch = createNewStoreWithoutBranch(storeAccountRequestDTO, storeAccountId);
            return StoreAccountResponseDTO.mapToDTO(newStoreWithoutBranch);
        }

        Store store = optionalStore.get();
        if (store.getKeycloakId() == null) {
            UUID storeAccountId = keycloakUserService.registerUser(storeAccountRequestDTO);
            log.info("Store with CNPJ {} exists without an associated account. Associating with new account {}",
                    storeAccountRequestDTO.getCnpj(), storeAccountId);

            store.setKeycloakId(storeAccountId);
            store.setEmail(storeAccountRequestDTO.getEmail());
            store.setUrlPhoto(storeAccountRequestDTO.getUrlPhoto());
            storeRepository.save(store);

            return StoreAccountResponseDTO.mapToDTO(store);
        }

        throw new RuntimeException("Store with CNPJ " + storeAccountRequestDTO.getCnpj() + " already exists and has an associated account.");
    }

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

    public StoreAccountResponseDTO getStoreAccountInfo(UUID keycloakId) {
        Store store = storeRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Store not found with Keycloak ID: " + keycloakId));
        return StoreAccountResponseDTO.mapToDTO(store);
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

    private Store createNewStoreWithoutBranch(StoreAccountRequestDTO storeDTO, UUID storeAccountId) {
        Store newStore = new Store();
        newStore.setCNPJ(storeDTO.getCnpj());
        newStore.setName(storeDTO.getName());
        newStore.setKeycloakId(storeAccountId);
        newStore.setEmail(storeDTO.getEmail());
        newStore.setUrlPhoto(storeDTO.getUrlPhoto());

        return storeRepository.save(newStore);
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

    public long getTotalRegisteredStore() {
        return storeRepository.count();
    }
}
