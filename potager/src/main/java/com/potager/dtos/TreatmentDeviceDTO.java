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
public class TreatmentDeviceDTO {
    private Long id;
    private Integer radius;
    private List<TreatmentProgramDTO> programs;
    private Long parcelId;
}