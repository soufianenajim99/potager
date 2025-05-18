package com.potager.Utils.mappers.interfaces;

import com.potager.dtos.*;
import com.potager.models.*;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GardenMapper {

    // Parcel mappings
    @Mapping(target = "xCoordinate", expression = "java(parcel.getXCoordinate())")
    @Mapping(target = "yCoordinate", expression = "java(parcel.getYCoordinate())")
    ParcelDTO parcelToParcelDTO(Parcel parcel);
    Parcel parcelDTOToParcel(ParcelDTO parcelDTO);

    // Plant mappings
    PlantDTO plantToPlantDTO(Plant plant);
    Plant plantDTOToPlant(PlantDTO plantDTO);

    // Insect mappings
    InsectDTO insectToInsectDTO(Insect insect);
    Insect insectDTOToInsect(InsectDTO insectDTO);

    // TreatmentDevice mappings
    TreatmentDeviceDTO treatmentDeviceToTreatmentDeviceDTO(TreatmentDevice device);
    TreatmentDevice treatmentDeviceDTOToTreatmentDevice(TreatmentDeviceDTO deviceDTO);

    // TreatmentProgram mappings
    TreatmentProgramDTO treatmentProgramToTreatmentProgramDTO(TreatmentProgram program);
    TreatmentProgram treatmentProgramDTOToTreatmentProgram(TreatmentProgramDTO programDTO);

    // List mappings
    List<ParcelDTO> parcelsToParcelDTOs(List<Parcel> parcels);
    List<PlantDTO> plantsToPlantDTOs(List<Plant> plants);
    List<InsectDTO> insectsToInsectDTOs(List<Insect> insects);

    // Custom mappings for complex cases
    @AfterMapping
    default void afterParcelMapping(Parcel parcel, @MappingTarget ParcelDTO parcelDTO) {
        if (parcel.getPlants() != null) {
            parcelDTO.setPlants(plantsToPlantDTOs(parcel.getPlants()));
        }
        if (parcel.getInsects() != null) {
            parcelDTO.setInsects(insectsToInsectDTOs(parcel.getInsects()));
        }
        if (parcel.getTreatmentDevice() != null) {
            parcelDTO.setTreatmentDevice(treatmentDeviceToTreatmentDeviceDTO(parcel.getTreatmentDevice()));
        }
    }

    @AfterMapping
    default void afterPlantMapping(Plant plant, @MappingTarget PlantDTO plantDTO) {
        if (plant.getParcel() != null) {
            plantDTO.setParcelId(plant.getParcel().getId());
        }
    }

    @AfterMapping
    default void afterTreatmentDeviceMapping(TreatmentDevice device, @MappingTarget TreatmentDeviceDTO deviceDTO) {
        if (device.getParcel() != null) {
            deviceDTO.setParcelId(device.getParcel().getId());
        }
        if (device.getPrograms() != null) {
            deviceDTO.setPrograms(
                    device.getPrograms().stream()
                            .map(this::treatmentProgramToTreatmentProgramDTO)
                            .collect(Collectors.toList())
            );
        }
    }
}
