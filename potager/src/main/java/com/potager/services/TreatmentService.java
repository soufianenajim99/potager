package com.potager.services;

import com.potager.Utils.mappers.interfaces.GardenMapper;
import com.potager.customExceptions.EntityNotFoundException;
import com.potager.dtos.ParcelDTO;
import com.potager.dtos.TreatmentDeviceDTO;
import com.potager.dtos.TreatmentProgramDTO;
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
public class TreatmentService {

    private final TreatmentDeviceRepository treatmentDeviceRepository;
    private final TreatmentProgramRepository treatmentProgramRepository;
    private final ParcelRepository parcelRepository;
    private final GardenMapper gardenMapper;

    public TreatmentDeviceDTO createTreatmentDevice(TreatmentDeviceDTO deviceDTO) {
        Parcel parcel = parcelRepository.findById(deviceDTO.getParcelId())
                .orElseThrow(() -> new EntityNotFoundException("Parcel not found with id: " + deviceDTO.getParcelId()));

        if (treatmentDeviceRepository.existsByParcelId(deviceDTO.getParcelId())) {
            throw new IllegalArgumentException("Parcel already has a treatment device");
        }

        TreatmentDevice device = new TreatmentDevice();
        device.setRadius(deviceDTO.getRadius());
        device.setParcel(parcel);

        TreatmentDevice savedDevice = treatmentDeviceRepository.save(device);
        return gardenMapper.treatmentDeviceToTreatmentDeviceDTO(savedDevice);
    }

    public TreatmentProgramDTO createTreatmentProgram(TreatmentProgramDTO programDTO) {
        TreatmentDevice device = treatmentDeviceRepository.findById(programDTO.getDeviceId())
                .orElseThrow(() -> new EntityNotFoundException("Treatment device not found with id: " + programDTO.getDeviceId()));

        TreatmentProgram program = new TreatmentProgram();
        program.setStartTime(programDTO.getStartTime());
        program.setDuration(programDTO.getDuration());
        program.setType(programDTO.getType());
        program.setDevice(device);

        TreatmentProgram savedProgram = treatmentProgramRepository.save(program);
        return gardenMapper.treatmentProgramToTreatmentProgramDTO(savedProgram);
    }

    public TreatmentDeviceDTO getDeviceByParcel(Long parcelId) {
        return treatmentDeviceRepository.findByParcelId(parcelId)
                .map(gardenMapper::treatmentDeviceToTreatmentDeviceDTO)
                .orElseThrow(() -> new EntityNotFoundException("No treatment device found for parcel id: " + parcelId));
    }

    public List<TreatmentProgramDTO> getProgramsByDevice(Long deviceId) {
        return treatmentProgramRepository.findByDeviceId(deviceId).stream()
                .map(gardenMapper::treatmentProgramToTreatmentProgramDTO)
                .collect(Collectors.toList());
    }

    public void activateTreatment(Long deviceId, int currentStep) {
        TreatmentDevice device = treatmentDeviceRepository.findById(deviceId)
                .orElseThrow(() -> new EntityNotFoundException("Treatment device not found with id: " + deviceId));

        device.getPrograms().stream()
                .filter(program -> isProgramActive(program, currentStep))
                .forEach(program -> {
                    List<Parcel> affectedParcels = getParcelsInRadius(device.getParcel(), device.getRadius());

                    switch (program.getType()) {
                        case WATER:
                            affectedParcels.forEach(p -> p.setHumidityLevel(Math.min(100, p.getHumidityLevel() + 20)));
                            parcelRepository.saveAll(affectedParcels);
                            break;

                        case FERTILIZER:
                            affectedParcels.forEach(p ->
                                    p.getPlants().forEach(plant ->
                                            plant.setCurrentAge(plant.getCurrentAge() + 1)));
                            break;

                        case INSECTICIDE:
                            affectedParcels.forEach(p ->
                                    p.getInsects().forEach(insect -> {
                                        if (Math.random() > insect.getInsecticideResistance()) {
                                            insect.setHealthIndex(0); // Kill insect
                                        }
                                    }));
                            break;
                    }
                });
    }

    private boolean isProgramActive(TreatmentProgram program, int currentStep) {
        return currentStep >= program.getStartTime() &&
                currentStep < program.getStartTime() + program.getDuration();
    }

    private List<Parcel> getParcelsInRadius(Parcel center, int radius) {
        int centerX = center.getXCoordinate();
        int centerY = center.getYCoordinate();

        return parcelRepository.findAll().stream()
                .filter(p -> {
                    int dx = Math.abs(p.getXCoordinate() - centerX);
                    int dy = Math.abs(p.getYCoordinate() - centerY);
                    return dx <= radius && dy <= radius;
                })
                .collect(Collectors.toList());
    }

    public void deleteTreatmentProgram(Long programId) {
        if (!treatmentProgramRepository.existsById(programId)) {
            throw new EntityNotFoundException("Treatment program not found with id: " + programId);
        }

        treatmentProgramRepository.deleteById(programId);
    }
    public void activateTreatments(int currentStep) {
        List<TreatmentDevice> devices = treatmentDeviceRepository.findAll();
        devices.forEach(device -> activateTreatment(device.getId(), currentStep));
    }
}
