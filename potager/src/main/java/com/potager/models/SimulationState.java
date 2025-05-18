package com.potager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "simulation_state")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentStep = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRunning = false;

    @Column(nullable = false)
    @Builder.Default
    private Double speedMultiplier = 1.0;
}