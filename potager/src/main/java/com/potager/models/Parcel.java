package com.potager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parcels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "xcoordinate", nullable = false)
    @Getter
    @Setter
    private Integer xCoordinate;

    @Column(name = "ycoordinate", nullable = false)
    @Getter @Setter
    private Integer yCoordinate;

    @Column(nullable = false)
    @Builder.Default
    private Double humidityLevel = 50.0;

    @OneToMany(mappedBy = "parcel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Plant> plants = new ArrayList<>();

    @OneToMany(mappedBy = "parcel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Insect> insects = new ArrayList<>();

    @OneToOne(mappedBy = "parcel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private TreatmentDevice treatmentDevice;
}
