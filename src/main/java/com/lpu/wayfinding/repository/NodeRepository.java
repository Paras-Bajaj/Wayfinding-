// src/main/java/com/lpu/wayfinding/repository/NodeRepository.java
package com.lpu.wayfinding.repository;

import com.lpu.wayfinding.entity.Node;
import com.lpu.wayfinding.util.NodeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NodeRepository extends JpaRepository<Node, Long> {
    List<Node> findByFloorId(Long floorId);
    List<Node> findByNodeType(NodeType nodeType);
}