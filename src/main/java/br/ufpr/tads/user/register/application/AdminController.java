package br.ufpr.tads.user.register.application;

import br.ufpr.tads.user.register.domain.request.UpdateCustomerAccountRequestDTO;
import br.ufpr.tads.user.register.domain.service.AdminService;
import br.ufpr.tads.user.register.domain.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/total-registered-users")
    public ResponseEntity<?> getTotalRegisteredUsers() {
        try {
            return ResponseEntity.ok(adminService.getTotalRegisteredUsers());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @GetMapping("/customer")
    public ResponseEntity<?> listCustomers(@RequestParam("page") int page, @RequestParam("size") int size,
                                           @RequestParam("sortDirection") Sort.Direction sortDirection,
                                           @RequestParam("sortBy") String sortBy) {
        try {
            log.info("Getting list of customers");
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(customerService.listCustomers(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @DeleteMapping("/customer/{keycloakId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("keycloakId") UUID keycloakId) {
        try {
            log.info("Deleting customers {}", keycloakId);
            customerService.deleteCustomer(keycloakId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PutMapping("/customer/{keycloakId}")
    public ResponseEntity<?> updateCustomer(@PathVariable("keycloakId") UUID keycloakId, @RequestBody UpdateCustomerAccountRequestDTO customerAccountRequestDTO) {
        try {
            log.info("Updating customers {}", keycloakId);
            customerService.updateCustomer(keycloakId, customerAccountRequestDTO);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

}
