// src/main/java/com/lpu/wayfinding/repository/EdgeRepository.java
package com.lpu.wayfinding.repository;

import com.lpu.wayfinding.entity.Edge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EdgeRepository extends JpaRepository<Edge, Long> {
    List<Edge> findBySourceId(Long sourceId);
    List<Edge> findBySourceIdIn(List<Long> sourceIds);
}