package br.ufpr.tads.user.register.application;

import br.ufpr.tads.user.register.domain.request.CustomerRequestDTO;
import br.ufpr.tads.user.register.domain.response.CustomerResponseDTO;
import br.ufpr.tads.user.register.domain.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/account/user")
public class UserController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<?> registerUser(@RequestBody CustomerRequestDTO customerRequestDTO) {
        try {
            log.info("Registering user {}", customerRequestDTO);
            return ResponseEntity.ok(customerService.registerCustomer(customerRequestDTO));
        } catch (Exception e) {
            log.info("User registration failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{keycloakId}")
    public CustomerResponseDTO getUser(@PathVariable String keycloakId) {
        return customerService.getCustomerByKeycloakId(UUID.fromString(keycloakId));
    }

}

