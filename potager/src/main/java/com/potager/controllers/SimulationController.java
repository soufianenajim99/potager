package com.potager.controllers;

import com.potager.customExceptions.EntityNotFoundException;
import com.potager.dtos.InsectDTO;
import com.potager.dtos.SimulationStateDTO;
import com.potager.services.InsectService;
import com.potager.services.SimulationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/api/simulation")
public class SimulationController extends GardenController {

    private final SimulationService simulationService;

    @GetMapping("/status")
    public ResponseEntity<SimulationStateDTO> getSimulationStatus() {
        logger.info("Fetching simulation status");
        return ok(simulationService.getSimulationStatus());
    }

    @PostMapping("/start")
    public ResponseEntity<Void> startSimulation() {
        logger.info("Starting simulation");
        simulationService.startSimulation();
        return ok();
    }

    @PostMapping("/pause")
    public ResponseEntity<Void> pauseSimulation() {
        logger.info("Pausing simulation");
        simulationService.pauseSimulation();
        return ok();
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> resetSimulation() {
        logger.info("Resetting simulation");
        simulationService.resetSimulation();
        return ok();
    }

    @PostMapping("/step")
    public ResponseEntity<Void> executeSingleStep() {
        logger.info("Executing single simulation step");
        simulationService.executeSingleStep();
        return ok();
    }

    @PutMapping("/speed")
    public ResponseEntity<Void> setSimulationSpeed(
            @RequestParam double speedMultiplier) {
        logger.info("Setting simulation speed to: {}", speedMultiplier);
        simulationService.setSimulationSpeed(speedMultiplier);
        return ok();
    }
}