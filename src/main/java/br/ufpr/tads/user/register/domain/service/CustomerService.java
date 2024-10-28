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

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = customerMapper.toEntity(customerRequestDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toDTO(savedCustomer);
    }
}
