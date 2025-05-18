package com.potager.models;

import com.potager.Utils.enums.TreatmentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "treatment_programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer startTime;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TreatmentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private TreatmentDevice device;
}