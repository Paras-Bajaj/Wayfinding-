// src/main/java/com/lpu/wayfinding/exception/NodeNotFoundException.java
package com.lpu.wayfinding.exception;

public class NodeNotFoundException extends WayfindingException {
    public NodeNotFoundException(Long id) {
        super("Node not found: " + id);
    }
}