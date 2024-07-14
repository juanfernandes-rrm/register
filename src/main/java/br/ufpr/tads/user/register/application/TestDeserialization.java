package br.ufpr.tads.user.register.application;

import br.ufpr.tads.user.register.domain.response.StoreDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestDeserialization {

    public static void main(String[] args) throws Exception {
        String json = "{\"id\":\"d9afad4b-a4ac-451a-96f7-16f808dba169\",\"name\":\"MERCADO SALLA LTDA\",\"address\":{\"street\":\"AVENIDA JURITI\",\"number\":\"321\",\"neighborhood\":\"JD CLAUDIA\",\"city\":\"PINHAIS\",\"state\":\"PR\"},\"cnpj\":\"11.522.691/0001-00\"}";

        ObjectMapper objectMapper = new ObjectMapper();
        StoreDTO storeDTO = objectMapper.readValue(json, StoreDTO.class);

        System.out.println(storeDTO.getName());
    }

    public static void test(String message){
        ObjectMapper objectMapper = new ObjectMapper();
        StoreDTO storeDTO = null;
        try {
            storeDTO = objectMapper.readValue(message, StoreDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println(storeDTO.getName());
    }

}
