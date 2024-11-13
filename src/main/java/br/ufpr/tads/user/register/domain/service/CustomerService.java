package br.ufpr.tads.user.register.domain.service;


import br.ufpr.tads.user.register.domain.model.Customer;
import br.ufpr.tads.user.register.domain.repository.CustomerRepository;
import br.ufpr.tads.user.register.domain.request.CustomerRequestDTO;
import br.ufpr.tads.user.register.domain.response.CustomerResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private KeycloakUserService keycloakUserService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SocialService socialService;

    @Autowired
    private UserRepository userRepository;

    //TODO: Add transaction implementation (create profile and register user)
    public CustomerResponseDTO registerCustomer(CustomerRequestDTO customerRequestDTO) {
        UUID userKeycloakId = UUID.fromString(keycloakUserService.registerUser(customerRequestDTO));
        Customer customer = mapToEntity(customerRequestDTO, userKeycloakId);
        Customer savedCustomer = customerRepository.save(customer);

        socialService.createProfile(userKeycloakId, keycloakUserService.getServiceAccountToken());

        return mapToDTO(savedCustomer);
    }

    public CustomerResponseDTO getCustomerByKeycloakId(UUID keycloakId) {
        return mapToDTO(customerRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public SliceImpl<CustomerResponseDTO> listCustomers(List<UUID> userIds, Pageable pageable) {
        Page<Customer> customerProfilesPage = customerRepository.findAllByKeycloakIdIn(userIds, pageable);

        List<CustomerResponseDTO> customerProfileDTOs = customerProfilesPage.stream()
                .map(this::mapToDTO)
                .toList();

        return new SliceImpl<>(customerProfileDTOs, pageable, customerProfilesPage.hasNext());
    }

    private Customer mapToEntity(CustomerRequestDTO customerRequestDTO, UUID userKeycloakId) {
        Customer customer = new Customer();
        customer.setKeycloakId(userKeycloakId);
        customer.setFirstName(customerRequestDTO.getFirstName());
        customer.setLastName(customerRequestDTO.getLastName());
        customer.setEmail(customerRequestDTO.getEmail());
        return customer;
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
