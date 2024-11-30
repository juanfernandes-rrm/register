package br.ufpr.tads.user.register.application;

import br.ufpr.tads.user.register.domain.request.CustomerAccountRequestDTO;
import br.ufpr.tads.user.register.domain.request.UpdateCustomerAccountRequestDTO;
import br.ufpr.tads.user.register.domain.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/account/user")
public class CustomerAccountController {

    @Autowired
    private CustomerService customerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerCustomer(@RequestPart CustomerAccountRequestDTO customerAccountRequestDTO, @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            log.info("Registering user {}", customerAccountRequestDTO);
            return ResponseEntity.ok(customerService.registerCustomerAccount(customerAccountRequestDTO, image));
        } catch (Exception e) {
            log.info("User registration failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/{keycloakId}")
    public ResponseEntity<?> getCustomer(@PathVariable String keycloakId) {
        return ResponseEntity.ok(customerService.getCustomerAccountByKeycloakId(UUID.fromString(keycloakId)));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCustomer(@RequestParam("page") int page, @RequestParam("size") int size,
                                            @RequestParam("sortDirection") Sort.Direction sortDirection,
                                            @RequestParam("sortBy") String sortBy,
                                            @RequestParam("firstname") String firstname) {
        try {
            log.info("Searching customer by firstname: {}", firstname);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(customerService.searchCustomerAccountByName(firstname, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('CUSTOMER')")
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateCustomer(@RequestPart UpdateCustomerAccountRequestDTO customerAccountRequestDTO,
                                            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            UUID user = getUser();
            log.info("Updating customers {}", user);
            customerService.updateCustomerAccount(user, customerAccountRequestDTO, image);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PostMapping("/details")
    public ResponseEntity<?> listCustomers(@RequestBody List<UUID> userIds, Pageable pageable) {
        return ResponseEntity.ok(customerService.listCustomerAccounts(userIds, pageable));
    }

    private UUID getUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("User: {}", jwt.getClaimAsString("preferred_username"));
        return UUID.fromString(jwt.getSubject());
    }

}

