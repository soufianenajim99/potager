package com.potager.controllers;

import com.potager.customExceptions.EntityNotFoundException;
import com.potager.dtos.InsectDTO;
import com.potager.dtos.TreatmentDeviceDTO;
import com.potager.dtos.TreatmentProgramDTO;
import com.potager.services.InsectService;
import com.potager.services.TreatmentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/treatments")
public class TreatmentController extends GardenController {

    private final TreatmentService treatmentService;

    @PostMapping("/devices")
    public ResponseEntity<?> createTreatmentDevice(
            @Valid @RequestBody TreatmentDeviceDTO deviceDTO) {
        logger.info("Creating new treatment device for parcel id: {}", deviceDTO.getParcelId());
        try {
            return created(treatmentService.createTreatmentDevice(deviceDTO));
        } catch (EntityNotFoundException e) {
            logger.warn("Failed to create treatment device: {}", e.getMessage());
            return notFound(e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to create treatment device: {}", e.getMessage());
            return badRequest(e.getMessage());
        }
    }

    @PostMapping("/programs")
    public ResponseEntity<?> createTreatmentProgram(
            @Valid @RequestBody TreatmentProgramDTO programDTO) {
        logger.info("Creating new treatment program for device id: {}", programDTO.getDeviceId());
        try {
            return created(treatmentService.createTreatmentProgram(programDTO));
        } catch (EntityNotFoundException e) {
            logger.warn("Failed to create treatment program: {}", e.getMessage());
            return notFound(e.getMessage());
        }
    }

    @GetMapping("/devices/parcel/{parcelId}")
    public ResponseEntity<?> getDeviceByParcel(@PathVariable Long parcelId) {
        logger.info("Fetching treatment device for parcel id: {}", parcelId);
        try {
            return ok(treatmentService.getDeviceByParcel(parcelId));
        } catch (EntityNotFoundException e) {
            logger.warn("Treatment device not found for parcel: {}", parcelId);
            return notFound(e.getMessage());
        }
    }
}
