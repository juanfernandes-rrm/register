package br.ufpr.tads.user.register.domain.service;


import br.ufpr.tads.user.register.domain.client.ImgurClient;
import br.ufpr.tads.user.register.domain.model.Customer;
import br.ufpr.tads.user.register.domain.repository.CustomerRepository;
import br.ufpr.tads.user.register.domain.request.CustomerAccountRequestDTO;
import br.ufpr.tads.user.register.domain.request.UpdateCustomerAccountRequestDTO;
import br.ufpr.tads.user.register.domain.response.CustomerAccountResponseDTO;
import jakarta.transaction.Transactional;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.nonNull;

@Service
public class CustomerService {

    @Autowired
    private KeycloakUserService keycloakUserService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SocialService socialService;

    @Autowired
    private ImageService imageService;

    //TODO: Need to implement saga pattern to rollback if any of the operations fail
    public CustomerAccountResponseDTO registerCustomerAccount(CustomerAccountRequestDTO customerAccountRequestDTO, MultipartFile image) {
        try {
            UUID userKeycloakId = keycloakUserService.registerUser(customerAccountRequestDTO);

            String urlPhoto = nonNull(image) ? imageService.uploadImage(image) : null;
            Customer customer = mapToEntity(customerAccountRequestDTO, userKeycloakId, urlPhoto);
            Customer savedCustomer = customerRepository.save(customer);

            socialService.createProfile(userKeycloakId, keycloakUserService.getServiceAccountToken());

            return mapToDTO(savedCustomer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CustomerAccountResponseDTO getCustomerAccountByKeycloakId(UUID keycloakId) {
        return mapToDTO(customerRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public SliceImpl<CustomerAccountResponseDTO> searchCustomerAccountByName(String name, Pageable pageable) {
        Slice<Customer> customerSlice = customerRepository.findByFirstNameContainingIgnoreCase(name, pageable);

        if (customerSlice.hasContent()) {
            List<CustomerAccountResponseDTO> customerProfileDTOs = customerSlice.stream()
                    .map(this::mapToDTO)
                    .toList();
            return new SliceImpl<>(customerProfileDTOs, customerSlice.getPageable(), customerSlice.hasNext());
        }

        return new SliceImpl<>(Collections.emptyList(), customerSlice.getPageable(), false);
    }

    public SliceImpl<CustomerAccountResponseDTO> listCustomerAccounts(Pageable pageable) {
        Page<Customer> customerProfilesPage = customerRepository.findAll(pageable);

        List<CustomerAccountResponseDTO> customerProfileDTOs = customerProfilesPage.stream()
                .map(this::mapToDTO)
                .toList();

        return new SliceImpl<>(customerProfileDTOs, customerProfilesPage.getPageable(), customerProfilesPage.hasNext());
    }

    public SliceImpl<CustomerAccountResponseDTO> listCustomerAccounts(List<UUID> userIds, Pageable pageable) {
        Page<Customer> customerProfilesPage = customerRepository.findAllByKeycloakIdIn(userIds, pageable);

        List<CustomerAccountResponseDTO> customerProfileDTOs = customerProfilesPage.stream()
                .map(this::mapToDTO)
                .toList();

        return new SliceImpl<>(customerProfileDTOs, customerProfilesPage.getPageable(), customerProfilesPage.hasNext());
    }

    public long getTotalRegisteredCustomers() {
        return customerRepository.count();
    }

    @Transactional
    //TODO: Need to implement saga pattern to rollback if any of the operations fail
    public void deleteAccountCustomer(UUID keycloakId) {
        keycloakUserService.deleteUserById(keycloakId.toString());
        customerRepository.findByKeycloakId(keycloakId).ifPresentOrElse(customerRepository::delete, () -> {
            throw new RuntimeException("User not found");
        });
        socialService.deleteProfile(keycloakId, keycloakUserService.getServiceAccountToken());
    }

    //TODO: Need to implement saga pattern to rollback if any of the operations fail
    public CustomerAccountResponseDTO updateCustomerAccount(UUID keycloakId, UpdateCustomerAccountRequestDTO customerAccountRequestDTO, MultipartFile image) {
        try {
            Customer customer = customerRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new RuntimeException("User not found"));

            String urlPhoto = nonNull(image) ? imageService.uploadImage(image) : customer.getUrlPhoto();

            String firstName = nonNull(customerAccountRequestDTO.getFirstName()) ? customerAccountRequestDTO.getFirstName() : customer.getFirstName();
            String lastName = nonNull(customerAccountRequestDTO.getLastName()) ? customerAccountRequestDTO.getLastName() : customer.getLastName();
            String email = nonNull(customerAccountRequestDTO.getEmail()) ? customerAccountRequestDTO.getEmail() : customer.getEmail();

            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setEmail(email);
            customer.setUrlPhoto(urlPhoto);
            customerRepository.save(customer);

            UserRepresentation userRepresentation = keycloakUserService.getUserById(keycloakId.toString());
            userRepresentation.setFirstName(firstName);
            userRepresentation.setLastName(lastName);
            userRepresentation.setEmail(email);
            keycloakUserService.updateUser(keycloakId.toString(), userRepresentation);

            if (nonNull(customerAccountRequestDTO.getPassword())) {
                keycloakUserService.updateUserPassword(keycloakId, customerAccountRequestDTO.getPassword());
            }

            return mapToDTO(customer);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user in Keycloak: " + e.getMessage(), e);
        }
    }

    private Customer mapToEntity(CustomerAccountRequestDTO customerAccountRequestDTO, UUID userKeycloakId, String urlPhoto) {
        Customer customer = new Customer();
        customer.setKeycloakId(userKeycloakId);
        customer.setFirstName(customerAccountRequestDTO.getFirstName());
        customer.setLastName(customerAccountRequestDTO.getLastName());
        customer.setEmail(customerAccountRequestDTO.getEmail());
        customer.setUrlPhoto(urlPhoto);
        return customer;
    }

    private CustomerAccountResponseDTO mapToDTO(Customer customer) {
        if (customer != null) {
            return CustomerAccountResponseDTO.builder()
                    .id(customer.getId())
                    .keycloakId(customer.getKeycloakId())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .email(customer.getEmail())
                    .urlPhoto(customer.getUrlPhoto())
                    .build();
        }
        return null;
    }

}
