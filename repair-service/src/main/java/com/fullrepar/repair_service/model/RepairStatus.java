package com.fullrepar.repair_service.model;

/**
 * Possible lifecycle states of a repair order.
 */
public enum RepairStatus {
    RECEIVED,
    IN_PROGRESS,
    COMPLETED,
    DELIVERED,
    CANCELLED
}
