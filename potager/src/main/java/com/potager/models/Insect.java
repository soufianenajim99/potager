package com.potager.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "insects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String species;

    @Column(nullable = false)
    private String sex;

    @Column(nullable = false)
    @Min(0) @Max(10)
    private Integer healthIndex = 10;

    @Column(nullable = false)
    private Double mobility;

    @Column(nullable = false)
    private Double insecticideResistance;

    @Column(nullable = false)
    @Builder.Default
    private Integer stepsWithoutFood = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;
}
