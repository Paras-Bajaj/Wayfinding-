// src/main/java/com/lpu/wayfinding/repository/PoiRepository.java
package com.lpu.wayfinding.repository;

import com.lpu.wayfinding.entity.Poi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PoiRepository extends JpaRepository<Poi, Long> {
    List<Poi> findByPoiType(String poiType);
}