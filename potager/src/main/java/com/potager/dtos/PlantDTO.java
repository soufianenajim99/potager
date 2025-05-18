package com.potager.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantDTO {
    private Long id;
    private String species;
    private Integer currentAge;
    private Integer maturityAge;
    private Boolean isRunner;
    private Double colonizationProbability;
    private Long parcelId;
}