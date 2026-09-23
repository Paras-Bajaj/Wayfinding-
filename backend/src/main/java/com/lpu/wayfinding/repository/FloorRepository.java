// src/main/java/com/lpu/wayfinding/repository/FloorRepository.java
package com.lpu.wayfinding.repository;

import com.lpu.wayfinding.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {
    List<Floor> findByBuildingId(Long buildingId);
}