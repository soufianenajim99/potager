package com.potager.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsectDTO {
    private Long id;
    private String species;
    private String sex;
    private Integer healthIndex;
    private Double mobility;
    private Double insecticideResistance;
    private Integer stepsWithoutFood;
    private Long parcelId;
}