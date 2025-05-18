package com.potager.controllers;

import com.potager.Utils.mappers.interfaces.GardenMapper;
import com.potager.customExceptions.EntityNotFoundException;
import com.potager.dtos.ParcelDTO;
import com.potager.models.Parcel;
import com.potager.repositories.ParcelRepository;
import com.potager.services.ParcelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/parcels")
public class ParcelController extends GardenController {

    private final ParcelService parcelService;
    private final ParcelRepository parcelRepository;
    private final GardenMapper gardenMapper;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping
    public ResponseEntity<List<ParcelDTO>> getAllParcels() {
        logger.info("Fetching all parcels");
        return ok(parcelService.getAllParcels());
    }
    @GetMapping("/raw")
    public ResponseEntity<List<Parcel>> getAllParcelsRaw() {
        return ok(parcelRepository.findAll());
    }

    @GetMapping("/test-mapping")
    public ResponseEntity<ParcelDTO> testMapping() {
        Parcel parcel = parcelRepository.findById(1L).orElseThrow();
        logger.info("Entity coordinates: {}, {}", parcel.getXCoordinate(), parcel.getYCoordinate());

        ParcelDTO dto = gardenMapper.parcelToParcelDTO(parcel);
        logger.info("DTO coordinates: {}, {}", dto.getXCoordinate(), dto.getYCoordinate());

        return ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getParcelById(@PathVariable Long id) {
        logger.info("Fetching parcel with id: {}", id);
        try {
            return ok(parcelService.getParcelById(id));
        } catch (EntityNotFoundException e) {
            logger.warn("Parcel not found with id: {}", id);
            return notFound(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createParcel(@Valid @RequestBody ParcelDTO parcelDTO) {
        logger.info("Creating new parcel at coordinates ({},{})",
                parcelDTO.getXCoordinate(), parcelDTO.getYCoordinate());
        try {
            return created(parcelService.createParcel(parcelDTO));
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create parcel: {}", e.getMessage());
            return badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcel(@PathVariable Long id) {
        logger.info("Deleting parcel with id: {}", id);
        parcelService.deleteParcel(id);
        return noContent();
    }

    @GetMapping("/dry")
    public ResponseEntity<List<ParcelDTO>> getDryParcels(
            @RequestParam(defaultValue = "30.0") double humidityThreshold) {
        logger.info("Fetching dry parcels with humidity below {}", humidityThreshold);
        return ok(parcelService.getDryParcels(humidityThreshold));
    }
}