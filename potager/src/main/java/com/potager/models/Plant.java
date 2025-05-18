package com.potager.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
@Entity
@Table(name = "plants")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String species;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentAge = 0;

    @Column(nullable = false)
    private Integer maturityAge;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRunner = false;

    @Column
    private Double colonizationProbability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;
}