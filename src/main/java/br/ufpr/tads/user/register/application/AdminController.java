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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/total-registered-users")
    public ResponseEntity<?> getTotalRegisteredUsers() {
        try {
            return ResponseEntity.ok(adminService.getTotalRegisteredUsers());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/customer")
    public ResponseEntity<?> listCustomers(@RequestParam("page") int page, @RequestParam("size") int size,
                                           @RequestParam("sortDirection") Sort.Direction sortDirection,
                                           @RequestParam("sortBy") String sortBy) {
        try {
            log.info("Getting list of customers");
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(customerService.listCustomerAccounts(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/customer/{keycloakId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("keycloakId") UUID keycloakId) {
        try {
            log.info("Deleting customers {}", keycloakId);
            customerService.deleteAccountCustomer(keycloakId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/customer/{keycloakId}")
    public ResponseEntity<?> updateCustomer(@PathVariable("keycloakId") UUID keycloakId, @RequestBody UpdateCustomerAccountRequestDTO customerAccountRequestDTO) {
        try {
            log.info("Updating customers {}", keycloakId);
            customerService.updateCustomerAccount(keycloakId, customerAccountRequestDTO);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/store")
    public ResponseEntity<?> listStores(@RequestParam("page") int page, @RequestParam("size") int size,
                                        @RequestParam("sortDirection") Sort.Direction sortDirection,
                                        @RequestParam("sortBy") String sortBy) {
        try {
            log.info("Getting list of stores");
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(storeService.listStoreAccounts(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/store/pending-approval")
    public ResponseEntity<?> listStoresPendingApproval(@RequestParam("page") int page, @RequestParam("size") int size,
                                                       @RequestParam("sortDirection") Sort.Direction sortDirection,
                                                       @RequestParam("sortBy") String sortBy) {
        try {
            log.info("Getting list of stores pending approval");
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(storeService.listStoreAccountsPendingApproval(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/store/{keycloakId}/approve")
    public ResponseEntity<?> approveStore(@PathVariable("keycloakId") UUID keycloakId) {
        try {
            log.info("Approving store {}", keycloakId);
            storeService.approveStoreAccount(keycloakId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping("/store/{keycloakId}/reject")
    public ResponseEntity<?> rejectStore(@PathVariable("keycloakId") UUID keycloakId) {
        try {
            log.info("Rejecting store {}", keycloakId);
            storeService.rejectStoreAccount(keycloakId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/store/{keycloakId}")
    public ResponseEntity<?> updateStore(@PathVariable("keycloakId") UUID keycloakId, @RequestBody UpdateStoreAccountRequestDTO storeAccountRequestDTO) {
        try {
            log.info("Updating store {}", keycloakId);
            return ResponseEntity.ok(storeService.updateStoreAccount(keycloakId, storeAccountRequestDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

}
