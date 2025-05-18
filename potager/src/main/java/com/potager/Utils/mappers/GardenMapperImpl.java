//package com.potager.Utils.mappers;
//
//import com.potager.Utils.mappers.interfaces.GardenMapper;
//import com.potager.dtos.*;
//import com.potager.models.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//@RequiredArgsConstructor
//public class GardenMapperImpl implements GardenMapper {
//
//    private final CustomMappings customMappings;
//
//    @Override
//    public ParcelDTO parcelToParcelDTO(Parcel parcel) {
//        if (parcel == null) {
//            return null;
//        }
//
//        ParcelDTO parcelDTO = new ParcelDTO();
//        parcelDTO.setId(parcel.getId());
//        parcelDTO.setXCoordinate(parcel.getXCoordinate());
//        parcelDTO.setYCoordinate(parcel.getYCoordinate());
//        parcelDTO.setHumidityLevel(parcel.getHumidityLevel());
//
//        return parcelDTO;
//    }
//
//    @Override
//    public Parcel parcelDTOToParcel(ParcelDTO parcelDTO) {
//        if (parcelDTO == null) {
//            return null;
//        }
//
//        Parcel parcel = new Parcel();
//        parcel.setId(parcelDTO.getId());
//        parcel.setXCoordinate(parcelDTO.getXCoordinate());
//        parcel.setYCoordinate(parcelDTO.getYCoordinate());
//        parcel.setHumidityLevel(parcelDTO.getHumidityLevel());
//
//        return parcel;
//    }
//
//    @Override
//    public PlantDTO plantToPlantDTO(Plant plant) {
//        if (plant == null) {
//            return null;
//        }
//
//        PlantDTO plantDTO = new PlantDTO();
//        plantDTO.setId(plant.getId());
//        plantDTO.setSpecies(plant.getSpecies());
//        plantDTO.setCurrentAge(plant.getCurrentAge());
//        plantDTO.setMaturityAge(plant.getMaturityAge());
//        plantDTO.setIsRunner(plant.getIsRunner());
//        plantDTO.setColonizationProbability(plant.getColonizationProbability());
//
//        return plantDTO;
//    }
//
//    @Override
//    public Plant plantDTOToPlant(PlantDTO plantDTO) {
//        return customMappings.toNewPlant(plantDTO);
//    }
//
//    @Override
//    public InsectDTO insectToInsectDTO(Insect insect) {
//        if (insect == null) {
//            return null;
//        }
//
//        InsectDTO insectDTO = new InsectDTO();
//        insectDTO.setId(insect.getId());
//        insectDTO.setSpecies(insect.getSpecies());
//        insectDTO.setSex(insect.getSex());
//        insectDTO.setHealthIndex(insect.getHealthIndex());
//        insectDTO.setMobility(insect.getMobility());
//        insectDTO.setInsecticideResistance(insect.getInsecticideResistance());
//        insectDTO.setStepsWithoutFood(insect.getStepsWithoutFood());
//
//        return insectDTO;
//    }
//
//    @Override
//    public Insect insectDTOToInsect(InsectDTO insectDTO) {
//        return customMappings.toNewInsect(insectDTO);
//    }
//
//    @Override
//    public TreatmentDeviceDTO treatmentDeviceToTreatmentDeviceDTO(TreatmentDevice device) {
//        if (device == null) {
//            return null;
//        }
//
//        TreatmentDeviceDTO deviceDTO = new TreatmentDeviceDTO();
//        deviceDTO.setId(device.getId());
//        deviceDTO.setRadius(device.getRadius());
//
//        return deviceDTO;
//    }
//
//    @Override
//    public TreatmentDevice treatmentDeviceDTOToTreatmentDevice(TreatmentDeviceDTO deviceDTO) {
//        return customMappings.toNewTreatmentDevice(deviceDTO);
//    }
//
//    @Override
//    public TreatmentProgramDTO treatmentProgramToTreatmentProgramDTO(TreatmentProgram program) {
//        if (program == null) {
//            return null;
//        }
//
//        TreatmentProgramDTO programDTO = new TreatmentProgramDTO();
//        programDTO.setId(program.getId());
//        programDTO.setStartTime(program.getStartTime());
//        programDTO.setDuration(program.getDuration());
//        programDTO.setType(program.getType());
//
//        return programDTO;
//    }
//
//    @Override
//    public TreatmentProgram treatmentProgramDTOToTreatmentProgram(TreatmentProgramDTO programDTO) {
//        if (programDTO == null) {
//            return null;
//        }
//
//        TreatmentProgram program = new TreatmentProgram();
//        program.setId(programDTO.getId());
//        program.setStartTime(programDTO.getStartTime());
//        program.setDuration(programDTO.getDuration());
//        program.setType(programDTO.getType());
//
//        return program;
//    }
//
//    @Override
//    public List<ParcelDTO> parcelsToParcelDTOs(List<Parcel> parcels) {
//        if (parcels == null) {
//            return null;
//        }
//
//        return parcels.stream()
//                .map(this::parcelToParcelDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<PlantDTO> plantsToPlantDTOs(List<Plant> plants) {
//        if (plants == null) {
//            return null;
//        }
//
//        return plants.stream()
//                .map(this::plantToPlantDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<InsectDTO> insectsToInsectDTOs(List<Insect> insects) {
//        if (insects == null) {
//            return null;
//        }
//
//        return insects.stream()
//                .map(this::insectToInsectDTO)
//                .collect(Collectors.toList());
//    }
//}
