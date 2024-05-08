package org.example.interpretationservice.service;

import org.example.interpretationservice.model.Beer;
import org.example.interpretationservice.repository.BeerRepository;
import org.example.interpretationservice.model.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class ImageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

    @Value("${image.service.url}")
    private String serviceUrl;

    private final BeerRepository beerRepository;

    public ImageService(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    public Beer processImage(MultipartFile file) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        body.add("image", resource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<ResponseWrapper> response = restTemplate.postForEntity(serviceUrl, requestEntity, ResponseWrapper.class);

        if (response.getBody() != null) {
            int classNumber = response.getBody().getPrediction();
            Beer beer = beerRepository.findByClassNumber(classNumber)
                    .orElseGet(() -> {
                        Beer newBeer = new Beer(null, classNumber, "Default Name", "Example Brewery", "Example Country", "Example Type", 5.0);
                        beerRepository.save(newBeer);
                        return newBeer;
                    });
            return beer;
        } else {
            logger.error("Failed to get valid response from the image service");
            throw new Exception("Invalid response from server");
        }
    }
}
