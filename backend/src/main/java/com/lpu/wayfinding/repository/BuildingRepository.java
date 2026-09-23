// src/main/java/com/lpu/wayfinding/repository/BuildingRepository.java
package com.lpu.wayfinding.repository;

import com.lpu.wayfinding.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {}