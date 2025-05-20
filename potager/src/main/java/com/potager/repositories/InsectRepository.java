package com.potager.repositories;

import com.potager.models.Insect;
import com.potager.models.TreatmentDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsectRepository extends JpaRepository<Insect, Long> {

    List<Insect> findBySpecies(String species);

    List<Insect> findByParcelId(Long parcelId);

    @Query("SELECT i FROM Insect i WHERE i.healthIndex > 5")
    List<Insect> findHealthyInsects();

    @Query("SELECT i FROM Insect i WHERE i.stepsWithoutFood >= 3")
    List<Insect> findHungryInsects();
}