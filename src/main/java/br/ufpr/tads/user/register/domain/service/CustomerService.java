package br.ufpr.tads.user.register.domain.service;


import br.ufpr.tads.user.register.domain.model.Customer;
import br.ufpr.tads.user.register.domain.repository.CustomerRepository;
import br.ufpr.tads.user.register.domain.request.CustomerAccountRequestDTO;
import br.ufpr.tads.user.register.domain.response.CustomerAccountResponseDTO;
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

    //TODO: Add transaction implementation (create profile and register user)
    public CustomerAccountResponseDTO registerCustomer(CustomerAccountRequestDTO customerAccountRequestDTO) {
        UUID userKeycloakId = keycloakUserService.registerUser(customerAccountRequestDTO);
        Customer customer = mapToEntity(customerAccountRequestDTO, userKeycloakId);
        Customer savedCustomer = customerRepository.save(customer);

        socialService.createProfile(userKeycloakId, keycloakUserService.getServiceAccountToken());

        return mapToDTO(savedCustomer);
    }

    public CustomerAccountResponseDTO getCustomerByKeycloakId(UUID keycloakId) {
        return mapToDTO(customerRepository.findByKeycloakId(keycloakId).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public SliceImpl<CustomerAccountResponseDTO> listCustomers(List<UUID> userIds, Pageable pageable) {
        Page<Customer> customerProfilesPage = customerRepository.findAllByKeycloakIdIn(userIds, pageable);

        List<CustomerAccountResponseDTO> customerProfileDTOs = customerProfilesPage.stream()
                .map(this::mapToDTO)
                .toList();

        return new SliceImpl<>(customerProfileDTOs, pageable, customerProfilesPage.hasNext());
    }

    private Customer mapToEntity(CustomerAccountRequestDTO customerAccountRequestDTO, UUID userKeycloakId) {
        Customer customer = new Customer();
        customer.setKeycloakId(userKeycloakId);
        customer.setFirstName(customerAccountRequestDTO.getFirstName());
        customer.setLastName(customerAccountRequestDTO.getLastName());
        customer.setEmail(customerAccountRequestDTO.getEmail());
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
                    .build();
        }
        return null;
    }

}
