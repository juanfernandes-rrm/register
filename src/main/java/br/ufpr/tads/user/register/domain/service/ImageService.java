package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.client.ImgurClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static java.util.Objects.nonNull;

@Service
public class ImageService {

    @Autowired
    private ImgurClient imgurClient;

    public String uploadImage(MultipartFile image) {
        try {
            return imgurClient.uploadImage(image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
