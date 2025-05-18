package com.potager.controllers;

import com.potager.customExceptions.EntityNotFoundException;
import com.potager.dtos.ParcelDTO;
import com.potager.dtos.PlantDTO;
import com.potager.services.ParcelService;
import com.potager.services.PlantService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/plants")
public class PlantController extends GardenController {

    private final PlantService plantService;

    @PostMapping
    public ResponseEntity<?> createPlant(@Valid @RequestBody PlantDTO plantDTO) {
        logger.info("Creating new plant of species: {}", plantDTO.getSpecies());
        try {
            return created(plantService.createPlant(plantDTO));
        } catch (EntityNotFoundException e) {
            logger.warn("Failed to create plant: {}", e.getMessage());
            return notFound(e.getMessage());
        }
    }

    @GetMapping("/parcel/{parcelId}")
    public ResponseEntity<List<PlantDTO>> getPlantsByParcel(@PathVariable Long parcelId) {
        logger.info("Fetching plants for parcel id: {}", parcelId);
        return ok(plantService.getPlantsByParcel(parcelId));
    }

    @GetMapping("/mature")
    public ResponseEntity<List<PlantDTO>> getMaturePlants() {
        logger.info("Fetching all mature plants");
        return ok(plantService.getMaturePlants());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> harvestPlant(@PathVariable Long id) {
        logger.info("Harvesting plant with id: {}", id);
        plantService.harvestPlant(id);
        return noContent();
    }
}
