package com.potager.controllers;

import com.potager.customExceptions.EntityNotFoundException;
import com.potager.dtos.InsectDTO;
import com.potager.services.InsectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/insects")
public class InsectController extends GardenController {

    private final InsectService insectService;

    @PostMapping
    public ResponseEntity<?> createInsect(@Valid @RequestBody InsectDTO insectDTO) {
        logger.info("Creating new insect of species: {}", insectDTO.getSpecies());
        try {
            return created(insectService.createInsect(insectDTO));
        } catch (EntityNotFoundException e) {
            logger.warn("Failed to create insect: {}", e.getMessage());
            return notFound(e.getMessage());
        }
    }

    @GetMapping("/parcel/{parcelId}")
    public ResponseEntity<List<InsectDTO>> getInsectsByParcel(@PathVariable Long parcelId) {
        logger.info("Fetching insects for parcel id: {}", parcelId);
        return ok(insectService.getInsectsByParcel(parcelId));
    }

    @GetMapping("/healthy")
    public ResponseEntity<List<InsectDTO>> getHealthyInsects() {
        logger.info("Fetching all healthy insects");
        return ok(insectService.getHealthyInsects());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeInsect(@PathVariable Long id) {
        logger.info("Removing insect with id: {}", id);
        insectService.removeInsect(id);
        return noContent();
    }
}
