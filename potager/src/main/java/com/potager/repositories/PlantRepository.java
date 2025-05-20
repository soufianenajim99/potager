package com.potager.repositories;

import com.potager.models.Plant;
import com.potager.models.TreatmentDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {

    List<Plant> findBySpecies(String species);

    List<Plant> findByIsRunnerTrue();
    List<Plant> findByParcelId(Long parcelId);

    @Query("SELECT p FROM Plant p WHERE p.currentAge >= p.maturityAge")
    List<Plant> findMaturePlants();
}
