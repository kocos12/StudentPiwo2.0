package org.example.interpretationservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Beers")
public record Beer(
        @Id String id,
        int classNumber,
        String name,
        String brewery,
        String country,
        String type,
        double alcoholContent
) {
}