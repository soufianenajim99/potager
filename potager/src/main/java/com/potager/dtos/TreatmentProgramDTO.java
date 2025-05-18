package com.potager.dtos;


import com.potager.Utils.enums.TreatmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentProgramDTO {

    private Long id;
    private Integer startTime;
    private Integer duration;
    private TreatmentType type;
    private Long deviceId;
}
