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
public class SimulationStateDTO {
    private Integer currentStep;
    private Boolean isRunning;
    private Double speedMultiplier;
}
