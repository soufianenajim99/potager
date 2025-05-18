package com.potager.dtos;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelDTO {
    private Long id;
    private Integer xCoordinate;
    private Integer yCoordinate;
    private Double humidityLevel;
    private List<PlantDTO> plants;
    private List<InsectDTO> insects;
    private TreatmentDeviceDTO treatmentDevice;
}