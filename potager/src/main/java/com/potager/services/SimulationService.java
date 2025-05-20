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
    private final SimulationStateRepository simulationStateRepository;

    private long lastExecutionTime = 0;

    private boolean shouldExecuteStep() {
        double multiplier = getCurrentSpeedMultiplier();
        long now = System.currentTimeMillis();
        long interval = Math.round(1000 / multiplier);
        if (now - lastExecutionTime >= interval) {
            lastExecutionTime = now;
            return true;
        }
        return false;
    }

    @Scheduled(fixedRate = 1000)
    public void runSimulation() {
        if (shouldExecuteStep()) {
            SimulationState state = getSimulationState();
            if (state.getIsRunning()) {
                executeSimulationStep(state);
            }
        }
    }


    public SimulationStateDTO getSimulationStatus() {
        SimulationState state = getSimulationState();
        return SimulationStateDTO.builder()
                .currentStep(state.getCurrentStep())
                .isRunning(state.getIsRunning())
                .speedMultiplier(state.getSpeedMultiplier())
                .build();
    }

    public void startSimulation() {
        SimulationState state = getSimulationState();
        state.setIsRunning(true);
        simulationStateRepository.save(state);
    }

    public void pauseSimulation() {
        SimulationState state = getSimulationState();
        state.setIsRunning(false);
        simulationStateRepository.save(state);
    }

    public void resetSimulation() {
        SimulationState state = getSimulationState();
        state.setCurrentStep(0);
        state.setIsRunning(false);
        simulationStateRepository.save(state);
    }

    public void executeSingleStep() {
        executeSimulationStep(getSimulationState());
    }

    public void setSimulationSpeed(double speedMultiplier) {
        if (speedMultiplier <= 0) {
            throw new IllegalArgumentException("Speed multiplier must be positive");
        }
        SimulationState state = getSimulationState();
        state.setSpeedMultiplier(speedMultiplier);
        simulationStateRepository.save(state);
    }

    public double getCurrentSpeedMultiplier() {
        return getSimulationState().getSpeedMultiplier();
    }

    private SimulationState getSimulationState() {
        // Get or create the single simulation state record
        return simulationStateRepository.findById(1L)
                .orElseGet(() -> simulationStateRepository.save(
                        SimulationState.builder()
                                .id(1L)
                                .currentStep(0)
                                .isRunning(false)
                                .speedMultiplier(1.0)
                                .build()));
    }

    private void executeSimulationStep(SimulationState state) {
        state.setCurrentStep(state.getCurrentStep() + 1);
        simulationStateRepository.save(state);

        updatePlants();
        updateInsects();
        treatmentService.activateTreatments(state.getCurrentStep());
        updateEnvironmentalConditions();
    }

    private void updatePlants() {
        List<Plant> plants = plantRepository.findAll();
        plants.forEach(plant -> {
            plant.setCurrentAge(plant.getCurrentAge() + 1);

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
                    insect.setHealthIndex(0);
                } else {
                    insect.setHealthIndex(Math.max(0, insect.getHealthIndex() - 1));
                }
            }

            if (Math.random() < insect.getMobility()) {
                moveInsectToAdjacentParcel(insect);
            }
        });

        List<Insect> deadInsects = insects.stream()
                .filter(i -> i.getHealthIndex() <= 0)
                .collect(Collectors.toList());

        insectRepository.deleteAll(deadInsects);

        List<Insect> aliveInsects = insects.stream()
                .filter(i -> i.getHealthIndex() > 0)
                .collect(Collectors.toList());

        insectRepository.saveAll(aliveInsects);
    }


    private void updateEnvironmentalConditions() {
        List<Parcel> parcels = parcelRepository.findAll();
        parcels.forEach(p -> p.setHumidityLevel(p.getHumidityLevel() * 0.98));
        parcelRepository.saveAll(parcels);
    }

    private void tryColonizeAdjacentParcel(Plant plant) {
        if (Math.random() < plant.getColonizationProbability()) {
            Parcel currentParcel = plant.getParcel();
            List<Parcel> adjacentParcels = findAdjacentParcels(currentParcel);

            adjacentParcels.stream()
                    .filter(p -> p.getPlants().isEmpty())
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