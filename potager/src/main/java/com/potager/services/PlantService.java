package com.potager.services;


import com.potager.Utils.mappers.interfaces.GardenMapper;
import com.potager.customExceptions.EntityNotFoundException;
import com.potager.dtos.ParcelDTO;
import com.potager.dtos.PlantDTO;
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
public class PlantService {

    private final PlantRepository plantRepository;
    private final ParcelRepository parcelRepository;
    private final GardenMapper gardenMapper;

    public PlantDTO createPlant(PlantDTO plantDTO) {
        Parcel parcel = parcelRepository.findById(plantDTO.getParcelId())
                .orElseThrow(() -> new EntityNotFoundException("Parcel not found"));

        Plant plant = new Plant();
        plant.setSpecies(plantDTO.getSpecies());
        plant.setMaturityAge(plantDTO.getMaturityAge());
        plant.setIsRunner(plantDTO.getIsRunner() != null ? plantDTO.getIsRunner() : false);
        plant.setColonizationProbability(plantDTO.getColonizationProbability());
        plant.setParcel(parcel);

        return gardenMapper.plantToPlantDTO(plantRepository.save(plant));
    }

    public List<PlantDTO> getPlantsByParcel(Long parcelId) {
        return plantRepository.findByParcelId(parcelId).stream()
                .map(gardenMapper::plantToPlantDTO)
                .collect(Collectors.toList());
    }

    public List<PlantDTO> getMaturePlants() {
        return plantRepository.findMaturePlants().stream()
                .map(gardenMapper::plantToPlantDTO)
                .collect(Collectors.toList());
    }

    public void harvestPlant(Long plantId) {
        plantRepository.deleteById(plantId);
    }
}