package com.tdsi.spring_multidatasource.service;


import com.tdsi.spring_multidatasource.entity.archive.ArchivedOrder;
import com.tdsi.spring_multidatasource.entity.operational.Order;
import com.tdsi.spring_multidatasource.repository.archive.ArchivedOrderRepository;
import com.tdsi.spring_multidatasource.repository.operational.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ===================================================================
 * Service Order - Orchestration Multi-DataSource
 * ===================================================================
 * Ce service montre comment utiliser les deux repositories
 * (operational et archive) dans la meme classe de service.
 *
 * Important :
 * - orderRepository utilise la transaction "operationalTransactionManager" (par defaut)
 * - archivedOrderRepository necessite @Transactional("archiveTransactionManager")
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ArchivedOrderRepository archivedOrderRepository;

    // ================================================================
    // Methodes CRUD - Base OPERATIONNELLE
    // ================================================================

    @Transactional  // TransactionManager operational (par defaut)
    public Order createOrder(Order order) {
        log.info("Creation d'une commande : {}", order.getOrderNumber());
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // ================================================================
    // Methodes CRUD - Base ARCHIVE
    // ================================================================

    @Transactional("archiveTransactionManager")
    public ArchivedOrder saveToArchive(ArchivedOrder archivedOrder) {
        log.info("Sauvegarde en archive : {}", archivedOrder.getOrderNumber());
        return archivedOrderRepository.save(archivedOrder);
    }

    @Transactional("archiveTransactionManager")
    public List<ArchivedOrder> saveAllToArchive(List<ArchivedOrder> archivedOrders) {
        log.info("Sauvegarde de {} commandes en archive", archivedOrders.size());
        return archivedOrderRepository.saveAll(archivedOrders);
    }

    @Transactional("archiveTransactionManager")
    public List<ArchivedOrder> getAllArchivedOrders() {
        return archivedOrderRepository.findAll();
    }

    @Transactional("archiveTransactionManager")
    public Optional<ArchivedOrder> getArchivedOrderByOriginalId(Long originalId) {
        return archivedOrderRepository.findByOriginalOrderId(originalId);
    }

    // ================================================================
    // Methodes Cross-Datasource (Operational -> Archive)
    // ================================================================

    /**
     * Archive une commande operationnelle vers la base d'archive.
     * Cette methode lit dans operational et ecrit dans archive.
     */
    @Transactional  // Transaction operational pour la lecture
    public ArchivedOrder archiveOrder(Long orderId) {
        // 1. Lecture dans la base OPERATIONNELLE
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvee : " + orderId));

        // 2. Verification : deja archivee ?
        if (archivedOrderRepository.existsByOriginalOrderId(orderId)) {
            throw new RuntimeException("Cette commande est deja archivee : " + orderId);
        }

        // 3. Conversion en entite archive
        ArchivedOrder archivedOrder = ArchivedOrder.builder()
                .originalOrderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .archivedDate(LocalDateTime.now())
                .originalCreatedAt(order.getCreatedAt())
                .archiveReason("Archivage automatique")
                .build();

        // 4. Ecriture dans la base ARCHIVE (nouvelle transaction)
        return saveToArchive(archivedOrder);
    }

    /**
     * Recupere les commandes archivees pour une periode
     */
    @Transactional("archiveTransactionManager")
    public List<ArchivedOrder> getArchivesByPeriod(LocalDateTime start, LocalDateTime end) {
        return archivedOrderRepository.findByArchivedDateBetween(start, end);
    }

    /**
     * Compte les commandes par statut dans les deux bases
     */
    public OrderStats getOrderStats() {
        long operationalCount = orderRepository.count();
        long archiveCount = archivedOrderRepository.count();

        return OrderStats.builder()
                .operationalOrderCount(operationalCount)
                .archivedOrderCount(archiveCount)
                .totalOrderCount(operationalCount + archiveCount)
                .build();
    }

    /**
     * DTO pour les statistiques
     */
    @lombok.Builder
    @lombok.Data
    public static class OrderStats {
        private long operationalOrderCount;
        private long archivedOrderCount;
        private long totalOrderCount;
    }
}
