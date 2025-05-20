package com.potager.repositories;

import com.potager.models.TreatmentDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentDeviceRepository extends JpaRepository<TreatmentDevice, Long> {

    Optional<TreatmentDevice> findByParcelId(Long parcelId);

    @Query("SELECT DISTINCT td FROM TreatmentDevice td JOIN td.programs p " +
            "WHERE :currentStep BETWEEN p.startTime AND p.startTime + p.duration")
    List<TreatmentDevice> findActiveDevices(@Param("currentStep") int currentStep);

    boolean existsByParcelId(Long parcelId);
}
