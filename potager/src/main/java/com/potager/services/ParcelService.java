package com.potager.services;

import com.potager.Utils.mappers.interfaces.GardenMapper;
import com.potager.customExceptions.EntityNotFoundException;
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
public class ParcelService {

    private final ParcelRepository parcelRepository;
    private final GardenMapper gardenMapper;

    public List<ParcelDTO> getAllParcels() {
        return parcelRepository.findAll().stream()
                .map(gardenMapper::parcelToParcelDTO)
                .collect(Collectors.toList());
    }

    public ParcelDTO getParcelById(Long id) {
        return parcelRepository.findById(id)
                .map(gardenMapper::parcelToParcelDTO)
                .orElseThrow(() -> new EntityNotFoundException("Parcel not found"));
    }

    public ParcelDTO createParcel(ParcelDTO parcelDTO) {
        if (parcelRepository.existsByxCoordinateAndyCoordinate(
                parcelDTO.getXCoordinate(), parcelDTO.getYCoordinate())) {
            throw new IllegalArgumentException("Parcel at these coordinates already exists");
        }

        Parcel parcel = new Parcel();
        parcel.setXCoordinate(parcelDTO.getXCoordinate());
        parcel.setYCoordinate(parcelDTO.getYCoordinate());
        parcel.setHumidityLevel(parcelDTO.getHumidityLevel() != null ?
                parcelDTO.getHumidityLevel() : 50.0);

        return gardenMapper.parcelToParcelDTO(parcelRepository.save(parcel));
    }

    public void deleteParcel(Long id) {
        parcelRepository.deleteById(id);
    }

    public List<ParcelDTO> getDryParcels(double humidityThreshold) {
        return parcelRepository.findDryParcels(humidityThreshold).stream()
                .map(gardenMapper::parcelToParcelDTO)
                .collect(Collectors.toList());
    }
}
