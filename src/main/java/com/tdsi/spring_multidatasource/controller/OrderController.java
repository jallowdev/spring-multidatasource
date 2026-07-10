package com.tdsi.spring_multidatasource.controller;


import com.tdsi.spring_multidatasource.entity.archive.ArchivedOrder;
import com.tdsi.spring_multidatasource.entity.operational.Order;
import com.tdsi.spring_multidatasource.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ===================================================================
 * Controller REST - Gestion des Commandes
 * ===================================================================
 * Expose les endpoints pour gerer les commandes dans les deux bases.
 */
@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    // ================================================================
    // Endpoints - Base OPERATIONNELLE
    // ================================================================

    @GetMapping("/operational")
    public ResponseEntity<List<Order>> getAllOrders() {
        log.info("GET /orders/operational");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/operational/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        log.info("GET /orders/operational/{}", id);
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/operational")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        log.info("POST /orders/operational - {}", order.getOrderNumber());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(order));
    }

    @GetMapping("/operational/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(
            @PathVariable Order.OrderStatus status) {
        log.info("GET /orders/operational/status/{}", status);
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    // ================================================================
    // Endpoints - Base ARCHIVE
    // ================================================================

    @GetMapping("/archive")
    public ResponseEntity<List<ArchivedOrder>> getAllArchivedOrders() {
        log.info("GET /orders/archive");
        return ResponseEntity.ok(orderService.getAllArchivedOrders());
    }

    @GetMapping("/archive/original/{originalId}")
    public ResponseEntity<ArchivedOrder> getArchivedOrderByOriginalId(
            @PathVariable Long originalId) {
        log.info("GET /orders/archive/original/{}", originalId);
        return orderService.getArchivedOrderByOriginalId(originalId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/archive/period")
    public ResponseEntity<List<ArchivedOrder>> getArchivesByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("GET /orders/archive/period?start={}&end={}", start, end);
        return ResponseEntity.ok(orderService.getArchivesByPeriod(start, end));
    }

    // ================================================================
    // Endpoints - Actions Cross-Datasource
    // ================================================================

    @PostMapping("/archive/{orderId}")
    public ResponseEntity<ArchivedOrder> archiveOrder(@PathVariable Long orderId) {
        log.info("POST /orders/archive/{}", orderId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.archiveOrder(orderId));
    }

    @GetMapping("/stats")
    public ResponseEntity<OrderService.OrderStats> getOrderStats() {
        log.info("GET /orders/stats");
        return ResponseEntity.ok(orderService.getOrderStats());
    }
}
