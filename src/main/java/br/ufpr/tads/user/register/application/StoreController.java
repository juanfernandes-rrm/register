package br.ufpr.tads.user.register.application;

import br.ufpr.tads.user.register.domain.response.StoreDTO;
import br.ufpr.tads.user.register.domain.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping
    public void userStore(@RequestBody StoreDTO storeDTO){
        storeService.createOrUpdateStore(storeDTO);
    }

}
