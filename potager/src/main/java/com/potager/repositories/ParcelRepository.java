package com.potager.repositories;

import com.potager.models.Parcel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    @Query("SELECT COUNT(p) > 0 FROM Parcel p WHERE p.xCoordinate = :x AND p.yCoordinate = :y")
    boolean existsByxCoordinateAndyCoordinate(@Param("x") int x, @Param("y") int y);

    @Query("SELECT p FROM Parcel p WHERE p.xCoordinate = :x AND p.yCoordinate = :y")
    Optional<Parcel> findByxCoordinateAndyCoordinate(@Param("x") int x, @Param("y") int y);

    @Query("SELECT p FROM Parcel p WHERE p.humidityLevel < :threshold")
    List<Parcel> findDryParcels(@Param("threshold") double threshold);

    @Query("SELECT p FROM Parcel p JOIN p.plants pl WHERE pl.currentAge >= pl.maturityAge")
    List<Parcel> findParcelsWithMaturePlants();
}
