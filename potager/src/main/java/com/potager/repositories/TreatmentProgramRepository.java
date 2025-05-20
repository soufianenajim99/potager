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

    List<TreatmentProgram> findByDeviceId(Long deviceId);

    @Query("SELECT p FROM TreatmentProgram p WHERE " +
            ":currentStep BETWEEN p.startTime AND p.startTime + p.duration")
    List<TreatmentProgram> findActivePrograms(@Param("currentStep") int currentStep);


    List<TreatmentProgram> findByType(TreatmentType type);


    @Query("SELECT p FROM TreatmentProgram p WHERE p.startTime > :step")
    List<TreatmentProgram> findFuturePrograms(@Param("step") int step);


    @Query("SELECT p FROM TreatmentProgram p WHERE p.startTime + p.duration < :step")
    List<TreatmentProgram> findCompletedPrograms(@Param("step") int step);


    @Query("SELECT COUNT(p) FROM TreatmentProgram p WHERE p.device.id = :deviceId")
    long countByDeviceId(@Param("deviceId") Long deviceId);


    @Query("SELECT p FROM TreatmentProgram p WHERE " +
            "p.device.id = :deviceId AND p.type = :type")
    List<TreatmentProgram> findByDeviceIdAndType(
            @Param("deviceId") Long deviceId,
            @Param("type") TreatmentType type);

    @Modifying
    @Query("DELETE FROM TreatmentProgram p WHERE p.device.id = :deviceId")
    void deleteByDeviceId(@Param("deviceId") Long deviceId);
}
