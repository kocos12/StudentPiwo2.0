package org.example.interpretationservice.service;

import org.example.interpretationservice.model.Beer;
import org.example.interpretationservice.repository.BeerRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BeerService {
    private final BeerRepository beerRepository;

    @Autowired
    public BeerService(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    public Beer addBeer(Beer beer) {
        return beerRepository.save(beer);
    }
}

