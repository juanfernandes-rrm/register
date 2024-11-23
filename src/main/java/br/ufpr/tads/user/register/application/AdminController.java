package br.ufpr.tads.user.register.application;

import br.ufpr.tads.user.register.domain.request.UpdateCustomerAccountRequestDTO;
import br.ufpr.tads.user.register.domain.request.UpdateStoreAccountRequestDTO;
import br.ufpr.tads.user.register.domain.service.AdminService;
import br.ufpr.tads.user.register.domain.service.CustomerService;
import br.ufpr.tads.user.register.domain.service.StoreService;
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

    @Autowired
    private StoreService storeService;

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

    @GetMapping("/store")
    public ResponseEntity<?> listStores(@RequestParam("page") int page, @RequestParam("size") int size,
                                        @RequestParam("sortDirection") Sort.Direction sortDirection,
                                        @RequestParam("sortBy") String sortBy) {
        try {
            log.info("Getting list of stores");
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(storeService.listStores(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @GetMapping("/store/pending-approval")
    public ResponseEntity<?> listStoresPendingApproval(@RequestParam("page") int page, @RequestParam("size") int size,
                                                       @RequestParam("sortDirection") Sort.Direction sortDirection,
                                                       @RequestParam("sortBy") String sortBy) {
        try {
            log.info("Getting list of stores pending approval");
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(storeService.listStoresPendingApproval(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PatchMapping("/store/{keycloakId}/approve")
    public ResponseEntity<?> approveStore(@PathVariable("keycloakId") UUID keycloakId) {
        try {
            log.info("Approving store {}", keycloakId);
            storeService.approveStore(keycloakId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PatchMapping("/store/{keycloakId}/reject")
    public ResponseEntity<?> rejectStore(@PathVariable("keycloakId") UUID keycloakId) {
        try {
            log.info("Rejecting store {}", keycloakId);
            storeService.rejectStore(keycloakId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PutMapping("/store/{keycloakId}")
    public ResponseEntity<?> updateStore(@PathVariable("keycloakId") UUID keycloakId, @RequestBody UpdateStoreAccountRequestDTO storeAccountRequestDTO) {
        try {
            log.info("Updating store {}", keycloakId);
            return ResponseEntity.ok(storeService.updateStore(keycloakId, storeAccountRequestDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

}
