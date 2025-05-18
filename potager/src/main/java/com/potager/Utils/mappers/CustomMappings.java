package com.potager.Utils.mappers;

import com.potager.dtos.InsectDTO;
import com.potager.dtos.PlantDTO;
import com.potager.dtos.TreatmentDeviceDTO;
import com.potager.models.Insect;
import com.potager.models.Plant;
import com.potager.models.TreatmentDevice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomMappings {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parcel", ignore = true)
    Plant toNewPlant(PlantDTO plantDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parcel", ignore = true)
    Insect toNewInsect(InsectDTO insectDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parcel", ignore = true)
    @Mapping(target = "programs", ignore = true)
    TreatmentDevice toNewTreatmentDevice(TreatmentDeviceDTO deviceDTO);
}