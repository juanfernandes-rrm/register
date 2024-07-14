package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.response.StoreDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StoreListener {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StoreService storeService;

    @RabbitListener(queues = "${broker.queue.receipt-scan.name}")
    public void listen(String message) {
        try {
            log.info(message);
            StoreDTO response = objectMapper.readValue(message, StoreDTO.class);
            log.info("Received message: "+response);
            storeService.createOrUpdateStore(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
