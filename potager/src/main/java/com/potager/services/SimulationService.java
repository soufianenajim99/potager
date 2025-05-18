package com.potager.services;

import com.potager.dtos.SimulationStateDTO;
import com.potager.models.*;
import com.potager.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SimulationService {

    private final ParcelRepository parcelRepository;
    private final PlantRepository plantRepository;
    private final InsectRepository insectRepository;
    private final TreatmentService treatmentService;

    private boolean isRunning = false;
    private int currentStep = 0;
    private double speedMultiplier = 1.0;

    @Scheduled(fixedRateString = "#{1000 / speedMultiplier}")
    public void runSimulation() {
        if (isRunning) {
            executeSimulationStep();
        }
    }

    public SimulationStateDTO getSimulationStatus() {
        return new SimulationStateDTO(currentStep, isRunning, speedMultiplier);
    }

    public void startSimulation() {
        this.isRunning = true;
    }

    public void pauseSimulation() {
        this.isRunning = false;
    }

    public void resetSimulation() {
        this.currentStep = 0;
        this.isRunning = false;
        // Add additional reset logic as needed
    }

    public void executeSingleStep() {
        executeSimulationStep();
    }

    public void setSimulationSpeed(double speedMultiplier) {
        if (speedMultiplier <= 0) {
            throw new IllegalArgumentException("Speed multiplier must be positive");
        }
        this.speedMultiplier = speedMultiplier;
    }

    private void executeSimulationStep() {
        currentStep++;

        // 1. Update all plants
        updatePlants();

        // 2. Update all insects
        updateInsects();

        // 3. Apply treatments
        treatmentService.activateTreatments(currentStep);

        // 4. Update environmental conditions
        updateEnvironmentalConditions();
    }

    private void updatePlants() {
        List<Plant> plants = plantRepository.findAll();
        plants.forEach(plant -> {
            plant.setCurrentAge(plant.getCurrentAge() + 1);

            // Handle runner plants colonization
            if (plant.getIsRunner() && plant.getColonizationProbability() != null) {
                tryColonizeAdjacentParcel(plant);
            }
        });
        plantRepository.saveAll(plants);
    }

    private void updateInsects() {
        List<Insect> insects = insectRepository.findAll();
        insects.forEach(insect -> {
            Parcel parcel = insect.getParcel();
            boolean hasFood = !parcel.getPlants().isEmpty();

            if (hasFood) {
                insect.setStepsWithoutFood(0);
            } else {
                insect.setStepsWithoutFood(insect.getStepsWithoutFood() + 1);
                if (insect.getStepsWithoutFood() >= 5) {
                    insect.setHealthIndex(0); // Insect dies
                } else {
                    insect.setHealthIndex(Math.max(0, insect.getHealthIndex() - 1));
                }
            }

            // Handle insect movement
            if (Math.random() < insect.getMobility()) {
                moveInsectToAdjacentParcel(insect);
            }
        });

        // Remove dead insects
        List<Insect> deadInsects = insects.stream()
                .filter(i -> i.getHealthIndex() <= 0)
                .collect(Collectors.toList());

        insectRepository.deleteAll(deadInsects);
        insectRepository.saveAll(insects);
    }

    private void updateEnvironmentalConditions() {
        // Natural humidity decay
        List<Parcel> parcels = parcelRepository.findAll();
        parcels.forEach(p -> p.setHumidityLevel(p.getHumidityLevel() * 0.98));
        parcelRepository.saveAll(parcels);
    }

    // Helper methods for plant colonization and insect movement...
    private void tryColonizeAdjacentParcel(Plant plant) {
        if (Math.random() < plant.getColonizationProbability()) {
            Parcel currentParcel = plant.getParcel();
            List<Parcel> adjacentParcels = findAdjacentParcels(currentParcel);

            adjacentParcels.stream()
                    .filter(p -> p.getPlants().isEmpty()) // Only colonize empty parcels
                    .findFirst()
                    .ifPresent(targetParcel -> {
                        Plant newPlant = new Plant();
                        newPlant.setSpecies(plant.getSpecies());
                        newPlant.setMaturityAge(plant.getMaturityAge());
                        newPlant.setIsRunner(true);
                        newPlant.setColonizationProbability(plant.getColonizationProbability());
                        newPlant.setParcel(targetParcel);
                        plantRepository.save(newPlant);
                    });
        }
    }

    private void moveInsectToAdjacentParcel(Insect insect) {
        Parcel currentParcel = insect.getParcel();
        List<Parcel> adjacentParcels = findAdjacentParcels(currentParcel);

        if (!adjacentParcels.isEmpty()) {
            Parcel targetParcel = adjacentParcels.get(
                    (int) (Math.random() * adjacentParcels.size()));
            insect.setParcel(targetParcel);
        }
    }

    private List<Parcel> findAdjacentParcels(Parcel parcel) {
        int x = parcel.getXCoordinate();
        int y = parcel.getYCoordinate();

        return Arrays.asList(
                        parcelRepository.findByxCoordinateAndyCoordinate(x+1, y),
                        parcelRepository.findByxCoordinateAndyCoordinate(x-1, y),
                        parcelRepository.findByxCoordinateAndyCoordinate(x, y+1),
                        parcelRepository.findByxCoordinateAndyCoordinate(x, y-1)
                ).stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
