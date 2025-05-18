package com.potager.repositories;


import com.potager.Utils.enums.TreatmentType;
import com.potager.models.TreatmentDevice;
import com.potager.models.TreatmentProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentProgramRepository extends JpaRepository<TreatmentProgram, Long> {

    // Find all programs for a specific device
    List<TreatmentProgram> findByDeviceId(Long deviceId);

    // Find active programs at a specific simulation step
    @Query("SELECT p FROM TreatmentProgram p WHERE " +
            ":currentStep BETWEEN p.startTime AND p.startTime + p.duration")
    List<TreatmentProgram> findActivePrograms(@Param("currentStep") int currentStep);

    // Find programs by type (WATER, FERTILIZER, INSECTICIDE)
    List<TreatmentProgram> findByType(TreatmentType type);

    // Find programs that will start after a specific step
    @Query("SELECT p FROM TreatmentProgram p WHERE p.startTime > :step")
    List<TreatmentProgram> findFuturePrograms(@Param("step") int step);

    // Find programs that have completed before a specific step
    @Query("SELECT p FROM TreatmentProgram p WHERE p.startTime + p.duration < :step")
    List<TreatmentProgram> findCompletedPrograms(@Param("step") int step);

    // Count programs by device
    @Query("SELECT COUNT(p) FROM TreatmentProgram p WHERE p.device.id = :deviceId")
    long countByDeviceId(@Param("deviceId") Long deviceId);

    // Find programs of a specific type for a device
    @Query("SELECT p FROM TreatmentProgram p WHERE " +
            "p.device.id = :deviceId AND p.type = :type")
    List<TreatmentProgram> findByDeviceIdAndType(
            @Param("deviceId") Long deviceId,
            @Param("type") TreatmentType type);

    // Delete all programs for a device
    @Modifying
    @Query("DELETE FROM TreatmentProgram p WHERE p.device.id = :deviceId")
    void deleteByDeviceId(@Param("deviceId") Long deviceId);
}
