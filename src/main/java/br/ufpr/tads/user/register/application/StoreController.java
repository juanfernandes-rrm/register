package br.ufpr.tads.user.register.application;

import br.ufpr.tads.user.register.domain.request.StoreAccountRequestDTO;
import br.ufpr.tads.user.register.domain.service.BranchService;
import br.ufpr.tads.user.register.domain.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @Autowired
    private BranchService branchService;

    @GetMapping("/branch/{correlationId}")
    public ResponseEntity<?> getStore(@PathVariable UUID correlationId){
        try {
            return ResponseEntity.ok(storeService.getStoreBranch(correlationId));
        }catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @GetMapping("/branch/nearby-from/{cep}")
    public ResponseEntity<?> getNearbyBranches(@PathVariable String cep, @RequestParam("distance") double distance){
        try {
            return ResponseEntity.ok(branchService.getNearbyBranch(cep, distance));
        }catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @GetMapping("/{keycloakId}")
    public ResponseEntity<?> getStoreAccountInfo(@PathVariable UUID keycloakId){
        try {
            return ResponseEntity.ok(storeService.getStoreAccountInfo(keycloakId));
        }catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> registerStore(@RequestBody StoreAccountRequestDTO storeAccountRequestDTO) {
        try {
            log.info("Registering store {}", storeAccountRequestDTO);
            return ResponseEntity.ok(storeService.registerStore(storeAccountRequestDTO));
        } catch (Exception e) {
            log.info("User registration failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
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
            return ResponseEntity.ok(storeService.searchByName(firstname, pageable));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

}
