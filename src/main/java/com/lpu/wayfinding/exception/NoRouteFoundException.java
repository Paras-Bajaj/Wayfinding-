// src/main/java/com/lpu/wayfinding/exception/NoRouteFoundException.java
package com.lpu.wayfinding.exception;

public class NoRouteFoundException extends WayfindingException {
    public NoRouteFoundException(Long start, Long end, String reason) {
        super(String.format("No route from %d to %d. %s", start, end, reason));
    }
}