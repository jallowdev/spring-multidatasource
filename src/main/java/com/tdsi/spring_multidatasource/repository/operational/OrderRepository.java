package com.tdsi.spring_multidatasource.repository.operational;

import com.tdsi.spring_multidatasource.entity.operational.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ===================================================================
 * Repository Order - Base OPERATIONNELLE
 * ===================================================================
 * Ce repository est automatiquement configure par Spring Data JPA
 * grace a @EnableJpaRepositories dans OperationalDataSourceConfig.
 *
 * Il utilise :
 * - L'EntityManagerFactory "operationalEntityManagerFactory"
 * - Le TransactionManager "operationalTransactionManager"
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Recherche par numero de commande
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Recherche par statut
     */
    List<Order> findByStatus(Order.OrderStatus status);

    /**
     * Recherche les commandes creees avant une certaine date
     * Utile pour identifier les commandes a archiver
     */
    List<Order> findByCreatedAtBefore(LocalDateTime date);

    /**
     * Recherche les commandes livrees ou annulees avant une date
     * (candidates a l'archivage)
     */
    @Query("SELECT o FROM Order o WHERE o.status IN ('DELIVERED', 'CANCELLED') " +
           "AND o.createdAt < :cutoffDate")
    List<Order> findOrdersToArchive(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Compte le nombre de commandes par statut
     */
    long countByStatus(Order.OrderStatus status);
}
