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

    public CustomerResponseDTO registerCustomer(CustomerRequestDTO customerRequestDTO) {
        UUID userKeycloakId = UUID.fromString(keycloakUserService.registerUser(customerRequestDTO));
        Customer customer = mapToEntity(customerRequestDTO, userKeycloakId);
        Customer savedCustomer = customerRepository.save(customer);
        return mapToDTO(savedCustomer);
    }

    public CustomerResponseDTO getCustomerById(UUID id) {
        return mapToDTO(customerRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    private Customer mapToEntity(CustomerRequestDTO customerRequestDTO, UUID userKeycloakId) {
        return Customer.builder()
                .keycloakId(userKeycloakId)
                .username(customerRequestDTO.getUsername())
                .email(customerRequestDTO.getEmail())
                .password(customerRequestDTO.getPassword())
                .build();
    }

    private CustomerResponseDTO mapToDTO(Customer customer) {
        if (customer != null) {
            return CustomerResponseDTO.builder()
                    .id(customer.getId())
                    .username(customer.getUsername())
                    .email(customer.getEmail())
                    .build();
        }
        return null;
    }

}
