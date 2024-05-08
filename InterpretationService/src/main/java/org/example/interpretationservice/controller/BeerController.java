package org.example.interpretationservice.controller;

import org.example.interpretationservice.model.Beer;
import org.example.interpretationservice.service.BeerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/beer")
public class BeerController {

    private final BeerService beerService;

    @Autowired
    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @PostMapping("/add")
    public ResponseEntity<Beer> addBeer(@RequestBody Beer beer) {
        Beer savedBeer = beerService.addBeer(beer);
        return ResponseEntity.ok(savedBeer);
    }
}

