package org.example.interpretationservice.repository;

import org.example.interpretationservice.model.Beer;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface BeerRepository extends MongoRepository<Beer, String> {
    Optional<Beer> findByClassNumber(int classNumber);
}