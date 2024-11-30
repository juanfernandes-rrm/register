package br.ufpr.tads.user.register.application;

import br.ufpr.tads.user.register.domain.request.StoreAccountRequestDTO;
import br.ufpr.tads.user.register.domain.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/account/store")
public class StoreAccountController {

    @Autowired
    private StoreService storeService;

    @GetMapping
    public ResponseEntity<?> getStoresAccounts(@RequestParam("page") int page, @RequestParam("size") int size,
                                               @RequestParam("sortDirection") Sort.Direction sortDirection,
                                               @RequestParam("sortBy") String sortBy) {
        try {
            log.info("Getting stores");
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(storeService.listStoreAccounts(pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchStore(@RequestParam("page") int page, @RequestParam("size") int size,
                                         @RequestParam("sortDirection") Sort.Direction sortDirection,
                                         @RequestParam("sortBy") String sortBy,
                                         @RequestParam("firstname") String firstname) {
        try {
            log.info("Searching store by firstname: {}", firstname);
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            return ResponseEntity.ok(storeService.searchStoreAccountByName(firstname, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStoreAccountInfo(@PathVariable UUID storeId){
        try {
            return ResponseEntity.ok(storeService.getStoreAccountInfo(storeId));
        }catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerStore(@RequestPart StoreAccountRequestDTO storeAccountRequestDTO,
                                           @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            log.info("Registering store {}", storeAccountRequestDTO);
            return ResponseEntity.ok(storeService.registerStoreAccount(storeAccountRequestDTO, image));
        } catch (Exception e) {
            log.info("User registration failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
