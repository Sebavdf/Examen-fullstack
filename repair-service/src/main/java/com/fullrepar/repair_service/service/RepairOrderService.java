package com.fullrepar.repair_service.service;

import com.fullrepar.repair_service.client.CustomerClient;
import com.fullrepar.repair_service.client.DeviceClient;
import com.fullrepar.repair_service.dto.RepairOrderRequest;
import com.fullrepar.repair_service.dto.RepairOrderResponse;
import com.fullrepar.repair_service.exception.BusinessException;
import com.fullrepar.repair_service.exception.ResourceNotFoundException;
import com.fullrepar.repair_service.model.RepairOrder;
import com.fullrepar.repair_service.model.RepairStatus;
import com.fullrepar.repair_service.repository.RepairOrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service class handling business logic for repair orders, the core domain
 * of the FullRepar ecosystem. Validates customer and device references through
 * remote calls and enforces the repair order status state machine.
 */
@Service
@RequiredArgsConstructor
public class RepairOrderService {

    private static final Logger log = LoggerFactory.getLogger(RepairOrderService.class);

    /**
     * Defines the allowed status transitions for a repair order.
     * A transition not present here is rejected as a business rule violation.
     */
    private static final Map<RepairStatus, Set<RepairStatus>> ALLOWED_TRANSITIONS = new EnumMap<>(RepairStatus.class);

    static {
        ALLOWED_TRANSITIONS.put(RepairStatus.RECEIVED, EnumSet.of(RepairStatus.IN_PROGRESS, RepairStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(RepairStatus.IN_PROGRESS, EnumSet.of(RepairStatus.COMPLETED, RepairStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(RepairStatus.COMPLETED, EnumSet.of(RepairStatus.DELIVERED));
        ALLOWED_TRANSITIONS.put(RepairStatus.DELIVERED, EnumSet.noneOf(RepairStatus.class));
        ALLOWED_TRANSITIONS.put(RepairStatus.CANCELLED, EnumSet.noneOf(RepairStatus.class));
    }

    private final RepairOrderRepository repairOrderRepository;
    private final CustomerClient customerClient;
    private final DeviceClient deviceClient;

    /**
     * Creates a new repair order after validating that both the customer and
     * the device exist in their respective microservices.
     *
     * @param request the repair order data.
     * @return the created repair order as a response DTO.
     */
    @Transactional
    public RepairOrderResponse create(RepairOrderRequest request) {
        log.info("Creating repair order for customer {} and device {}", request.customerId(), request.deviceId());

        customerClient.validateCustomerExists(request.customerId());
        deviceClient.validateDeviceExists(request.deviceId());

        RepairOrder order = RepairOrder.builder()
                .customerId(request.customerId())
                .deviceId(request.deviceId())
                .issueDescription(request.issueDescription())
                .estimatedCost(request.estimatedCost())
                .status(RepairStatus.RECEIVED)
                .build();

        RepairOrder saved = repairOrderRepository.save(order);
        log.info("Repair order created with ID: {}", saved.getId());
        return toResponse(saved);
    }

    public List<RepairOrderResponse> findAll() {
        return repairOrderRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<RepairOrderResponse> findByCustomerId(Long customerId) {
        return repairOrderRepository.findByCustomerId(customerId).stream().map(this::toResponse).toList();
    }

    public RepairOrderResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    /**
     * Transitions a repair order to a new status, enforcing the valid state
     * machine of the domain (e.g. a DELIVERED order can never change again).
     *
     * @param id the repair order id.
     * @param newStatus the requested new status.
     * @return the updated repair order.
     */
    @Transactional
    public RepairOrderResponse updateStatus(Long id, RepairStatus newStatus) {
        RepairOrder order = getOrThrow(id);
        Set<RepairStatus> allowed = ALLOWED_TRANSITIONS.get(order.getStatus());

        if (allowed == null || !allowed.contains(newStatus)) {
            log.warn("Invalid transition from {} to {} for order {}", order.getStatus(), newStatus, id);
            throw new BusinessException(
                    "Cannot transition repair order from " + order.getStatus() + " to " + newStatus);
        }

        order.setStatus(newStatus);
        RepairOrder updated = repairOrderRepository.save(order);
        log.info("Repair order {} transitioned to {}", id, newStatus);
        return toResponse(updated);
    }

    /**
     * Checks whether a repair order exists and is in COMPLETED status,
     * used by review-service and payment-service validation flows.
     */
    public boolean isCompleted(Long id) {
        return repairOrderRepository.findById(id)
                .map(order -> order.getStatus() == RepairStatus.COMPLETED
                        || order.getStatus() == RepairStatus.DELIVERED)
                .orElse(false);
    }

    private RepairOrder getOrThrow(Long id) {
        return repairOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repair order not found with id: " + id));
    }

    private RepairOrderResponse toResponse(RepairOrder order) {
        return new RepairOrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getDeviceId(),
                order.getIssueDescription(),
                order.getStatus(),
                order.getEstimatedCost(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
