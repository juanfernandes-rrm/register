package br.ufpr.tads.user.register.application;

import br.ufpr.tads.user.register.domain.request.CustomerAccountRequestDTO;
import br.ufpr.tads.user.register.domain.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/account/user")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerAccountRequestDTO customerAccountRequestDTO) {
        try {
            log.info("Registering user {}", customerAccountRequestDTO);
            return ResponseEntity.ok(customerService.registerCustomer(customerAccountRequestDTO));
        } catch (Exception e) {
            log.info("User registration failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{keycloakId}")
    public ResponseEntity<?> getCustomer(@PathVariable String keycloakId) {
        return ResponseEntity.ok(customerService.getCustomerByKeycloakId(UUID.fromString(keycloakId)));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCustomer(@RequestParam("page") int page, @RequestParam("size") int size,
                                            @RequestParam("sortDirection") Sort.Direction sortDirection,
                                            @RequestParam("sortBy") String sortBy,
                                            @RequestParam("firstname") String firstname) {
        try {
            log.info("Searching customer by firstname: {}", firstname);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(customerService.searchCustomerByName(firstname, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PostMapping("/details")
    public ResponseEntity<?> listCustomers(@RequestBody List<UUID> userIds, Pageable pageable) {
        return ResponseEntity.ok(customerService.listCustomers(userIds, pageable));
    }

}

