package br.ufpr.tads.user.register.domain.service;


import br.ufpr.tads.user.register.domain.model.Customer;
import br.ufpr.tads.user.register.domain.repository.CustomerRepository;
import br.ufpr.tads.user.register.domain.request.CustomerRequestDTO;
import br.ufpr.tads.user.register.domain.response.CustomerResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private KeycloakUserService keycloakUserService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SocialService socialService;

    //TODO: Add transaction implementation (create profile and register user)
    public CustomerResponseDTO registerCustomer(CustomerRequestDTO customerRequestDTO) {
        UUID userKeycloakId = UUID.fromString(keycloakUserService.registerUser(customerRequestDTO));
        Customer customer = mapToEntity(customerRequestDTO, userKeycloakId);
        Customer savedCustomer = customerRepository.save(customer);

        socialService.createProfile(userKeycloakId, keycloakUserService.getServiceAccountToken());

        return mapToDTO(savedCustomer);
    }


    public CustomerResponseDTO getCustomerById(UUID id) {
        return mapToDTO(customerRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public CustomerResponseDTO getCustomerByKeycloakId(UUID keycloakId) {
        return mapToDTO(customerRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new RuntimeException("User not found")));
    }

    private Customer mapToEntity(CustomerRequestDTO customerRequestDTO, UUID userKeycloakId) {
        return Customer.builder()
                .keycloakId(userKeycloakId)
                .firstName(customerRequestDTO.getFirstName())
                .lastName(customerRequestDTO.getLastName())
                .email(customerRequestDTO.getEmail())
                .build();
    }

    private CustomerResponseDTO mapToDTO(Customer customer) {
        if (customer != null) {
            return CustomerResponseDTO.builder()
                    .id(customer.getId())
                    .keycloakId(customer.getKeycloakId())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .email(customer.getEmail())
                    .build();
        }
        return null;
    }

}
