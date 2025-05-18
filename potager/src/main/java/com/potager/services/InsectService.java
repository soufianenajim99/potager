package com.potager.services;

import com.potager.Utils.mappers.interfaces.GardenMapper;
import com.potager.customExceptions.EntityNotFoundException;
import com.potager.dtos.InsectDTO;
import com.potager.dtos.ParcelDTO;
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
public class InsectService {

    private final InsectRepository insectRepository;
    private final ParcelRepository parcelRepository;
    private final GardenMapper gardenMapper;

    public InsectDTO createInsect(InsectDTO insectDTO) {
        Parcel parcel = parcelRepository.findById(insectDTO.getParcelId())
                .orElseThrow(() -> new EntityNotFoundException("Parcel not found with id: " + insectDTO.getParcelId()));

        Insect insect = new Insect();
        insect.setSpecies(insectDTO.getSpecies());
        insect.setSex(insectDTO.getSex());
        insect.setHealthIndex(insectDTO.getHealthIndex() != null ? insectDTO.getHealthIndex() : 10);
        insect.setMobility(insectDTO.getMobility());
        insect.setInsecticideResistance(insectDTO.getInsecticideResistance());
        insect.setStepsWithoutFood(0);
        insect.setParcel(parcel);

        Insect savedInsect = insectRepository.save(insect);
        return gardenMapper.insectToInsectDTO(savedInsect);
    }

    public List<InsectDTO> getInsectsByParcel(Long parcelId) {
        return insectRepository.findByParcelId(parcelId).stream()
                .map(gardenMapper::insectToInsectDTO)
                .collect(Collectors.toList());
    }

    public List<InsectDTO> getHealthyInsects() {
        return insectRepository.findHealthyInsects().stream()
                .map(gardenMapper::insectToInsectDTO)
                .collect(Collectors.toList());
    }

    public List<InsectDTO> getHungryInsects() {
        return insectRepository.findHungryInsects().stream()
                .map(gardenMapper::insectToInsectDTO)
                .collect(Collectors.toList());
    }

    public void removeInsect(Long insectId) {
        if (!insectRepository.existsById(insectId)) {
            throw new EntityNotFoundException("Insect not found with id: " + insectId);
        }
        insectRepository.deleteById(insectId);
    }

    public void moveInsectToParcel(Long insectId, Long parcelId) {
        Insect insect = insectRepository.findById(insectId)
                .orElseThrow(() -> new EntityNotFoundException("Insect not found with id: " + insectId));

        Parcel targetParcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new EntityNotFoundException("Parcel not found with id: " + parcelId));

        insect.setParcel(targetParcel);
        insectRepository.save(insect);
    }

    public void applyInsecticideEffect(Long parcelId, double effectiveness) {
        List<Insect> insects = insectRepository.findByParcelId(parcelId);

        insects.forEach(insect -> {
            if (Math.random() > insect.getInsecticideResistance()) {
                // Insect dies if random number is greater than its resistance
                insect.setHealthIndex(0);
            } else {
                // Otherwise, it takes some damage
                int damage = (int) (Math.random() * effectiveness * 5);
                insect.setHealthIndex(Math.max(0, insect.getHealthIndex() - damage));
            }
        });

        // Remove dead insects
        List<Insect> deadInsects = insects.stream()
                .filter(i -> i.getHealthIndex() <= 0)
                .collect(Collectors.toList());

        insectRepository.deleteAll(deadInsects);
        insectRepository.saveAll(insects);
    }
}
