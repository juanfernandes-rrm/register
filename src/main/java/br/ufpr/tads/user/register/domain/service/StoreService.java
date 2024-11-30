package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.model.Address;
import br.ufpr.tads.user.register.domain.model.Branch;
import br.ufpr.tads.user.register.domain.model.Store;
import br.ufpr.tads.user.register.domain.repository.BranchRepository;
import br.ufpr.tads.user.register.domain.repository.StoreRepository;
import br.ufpr.tads.user.register.domain.request.StoreAccountRequestDTO;
import br.ufpr.tads.user.register.domain.request.UpdateStoreAccountRequestDTO;
import br.ufpr.tads.user.register.domain.response.AddressDTO;
import br.ufpr.tads.user.register.domain.response.BranchDTO;
import br.ufpr.tads.user.register.domain.response.StoreAccountResponseDTO;
import br.ufpr.tads.user.register.domain.response.StoreDTO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
    private BranchService branchService;

    @Autowired
    private KeycloakUserService keycloakUserService;

    public StoreAccountResponseDTO registerStoreAccount(StoreAccountRequestDTO storeAccountRequestDTO) {
        Optional<Store> optionalStore = storeRepository.findByCnpjRoot(storeAccountRequestDTO.getCnpj());

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
        String cnpjRoot = extractCnpjRoot(storeDTO.getCnpj());

        Optional<Store> existingStore = storeRepository.findByCnpjRootWithLock(cnpjRoot);

        if (existingStore.isPresent()) {
            log.info("Store with CNPJ root {} already exists. Adding new branch with CNPJ {}", cnpjRoot, storeDTO.getCnpj());
            return addNewBranchToExistingStore(existingStore.get(), storeDTO);
        }

        log.info("Store with CNPJ root {} does not exist. Creating new store with branch", cnpjRoot);
        return createNewStoreWithBranch(storeDTO);
    }


    public BranchDTO getStoreBranch(UUID correlationId) {
        Optional<Branch> branchOptional = branchRepository.findByCorrelationId(correlationId);

        if (branchOptional.isPresent()) {
            return branchOptional.map(this::mapBranchToDTO).get();
        }

        log.info("Branch with correlationId {} not found", correlationId);
        throw new RuntimeException("Branch not found with correlationId: " + correlationId);
    }

    public StoreAccountResponseDTO getStoreAccountInfo(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store with Id {} not found." + storeId));
        return StoreAccountResponseDTO.mapToDTO(store);
    }

    public long getTotalRegisteredStore() {
        return storeRepository.count();
    }

    public SliceImpl<StoreAccountResponseDTO> listStoreAccounts(Pageable pageable) {
        Slice<Store> storeSlice = storeRepository.findAll(pageable);

        if (storeSlice.hasContent()) {
            List<StoreAccountResponseDTO> storeAccountResponseDTOs = storeSlice.stream()
                    .map(StoreAccountResponseDTO::mapToDTO)
                    .toList();

            return new SliceImpl<>(storeAccountResponseDTOs, storeSlice.getPageable(), storeSlice.hasNext());
        }

        return new SliceImpl<>(List.of(), pageable, false);
    }

    public SliceImpl<StoreAccountResponseDTO> searchStoreAccountByName(String firstname, Pageable pageable) {
        Slice<Store> storeSlice = storeRepository.findByNameContainingIgnoreCase(firstname, pageable);

        if (storeSlice.hasContent()) {
            List<StoreAccountResponseDTO> storeAccountResponseDTOs = storeSlice.stream()
                    .map(StoreAccountResponseDTO::mapToDTO)
                    .toList();

            return new SliceImpl<>(storeAccountResponseDTOs, storeSlice.getPageable(), storeSlice.hasNext());
        }

        return new SliceImpl<>(List.of(), pageable, false);
    }

    public SliceImpl<StoreAccountResponseDTO> listStoreAccountsPendingApproval(Pageable pageable) {
        Slice<Store> storeSlice = storeRepository.findByApprovedNullAndKeycloakIdNotNull(pageable);

        if (storeSlice.hasContent()) {
            List<StoreAccountResponseDTO> storeAccountResponseDTOs = storeSlice.stream()
                    .map(StoreAccountResponseDTO::mapToDTO)
                    .toList();

            return new SliceImpl<>(storeAccountResponseDTOs, storeSlice.getPageable(), storeSlice.hasNext());
        }

        return new SliceImpl<>(List.of(), pageable, false);
    }

    public void approveStoreAccount(UUID keycloakId) {
        Store store = storeRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Store not found with keycloakId: " + keycloakId));

        store.setApproved(true);
        storeRepository.save(store);
    }

    public void rejectStoreAccount(UUID keycloakId) {
        Store store = storeRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Store not found with keycloakId: " + keycloakId));

        store.setApproved(false);
        storeRepository.save(store);
    }

    public StoreAccountResponseDTO updateStoreAccount(UUID keycloakId, UpdateStoreAccountRequestDTO storeAccountRequestDTO) {
        Store store = storeRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("Store not found with keycloakId: " + keycloakId));

        store.setName(storeAccountRequestDTO.getName());
        store.setEmail(storeAccountRequestDTO.getEmail());
        store.setUrlPhoto(storeAccountRequestDTO.getUrlPhoto());
        StoreAccountResponseDTO updatedStoreAccountResponseDTO = StoreAccountResponseDTO.mapToDTO(storeRepository.save(store));

        try {
            UserRepresentation userRepresentation = keycloakUserService.getUserById(keycloakId.toString());
            userRepresentation.setFirstName(storeAccountRequestDTO.getName());
            userRepresentation.setEmail(store.getEmail());
            keycloakUserService.updateUser(keycloakId.toString(), userRepresentation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user in Keycloak: " + e.getMessage(), e);
        }

        return updatedStoreAccountResponseDTO;
    }

    public SliceImpl<BranchDTO> getBranches(UUID storeId, Pageable pageable) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new RuntimeException("Store not found with storeId: " + storeId));

        SliceImpl<Branch> branchSlice = branchService.getStoreBranch(store, pageable);
        List<BranchDTO> branchDTOList = branchSlice.stream()
                .map(this::mapBranchToDTO)
                .toList();

        return new SliceImpl<>(branchDTOList, branchSlice.getPageable(), branchSlice.hasNext());
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
        newStore.setCnpjRoot(extractCnpjRoot(storeDTO.getCnpj()));
        newStore.setName(storeDTO.getName());

        Branch newBranch = convertToBranch(storeDTO);
        newBranch.setStore(newStore);

        newStore.setBranches(List.of(newBranch));

        storeRepository.save(newStore);
        return newBranch;
    }

    private Store createNewStoreWithoutBranch(StoreAccountRequestDTO storeDTO, UUID storeAccountId) {
        Store newStore = new Store();
        newStore.setCnpjRoot(extractCnpjRoot(storeDTO.getCnpj()));
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
        branch.setCpnj(storeDTO.getCnpj());
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
        storeDTO.setCnpj(store.getCnpjRoot());
        storeDTO.setName(store.getName());
        storeDTO.setAddress(addressDTO);

        BranchDTO branchDTO = new BranchDTO();
        branchDTO.setId(branch.getId());
        branchDTO.setStore(storeDTO);
        branchDTO.setCorrelationId(branch.getCorrelationId());

        return branchDTO;
    }

    private String extractCnpjRoot(String cnpj) {
        if (cnpj == null || cnpj.length() < 8) {
            throw new IllegalArgumentException("CNPJ inválido: " + cnpj);
        }
        return cnpj.substring(0, 8);
    }
    
}
