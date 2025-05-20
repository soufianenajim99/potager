package com.potager.repositories;

import com.potager.models.SimulationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationStateRepository extends JpaRepository<SimulationState, Long> {
}
