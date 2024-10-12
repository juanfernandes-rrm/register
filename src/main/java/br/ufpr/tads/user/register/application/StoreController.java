package br.ufpr.tads.user.register.application;

import br.ufpr.tads.user.register.domain.response.StoreDTO;
import br.ufpr.tads.user.register.domain.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping
    public void userStore(@RequestBody StoreDTO storeDTO){
        storeService.createOrUpdateStore(storeDTO);
    }

    @GetMapping("/branch/{correlationId}")
    public ResponseEntity<?> getStore(@PathVariable UUID correlationId){
        try {
            return ResponseEntity.ok(storeService.getStoreBranch(correlationId));
        }catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

}
