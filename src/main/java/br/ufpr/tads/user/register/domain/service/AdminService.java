package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.response.TotalRegisteredUsersResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private StoreService storeService;


    public TotalRegisteredUsersResponseDTO getTotalRegisteredUsers() {
        return new TotalRegisteredUsersResponseDTO(customerService.getTotalRegisteredCustomers(),
                storeService.getTotalRegisteredStore());
    }

}
