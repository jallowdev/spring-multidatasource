package com.tdsi.spring_multidatasource.repository.archive;

import com.tdsi.spring_multidatasource.entity.archive.ArchivedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ===================================================================
 * Repository ArchivedOrder - Base ARCHIVE
 * ===================================================================
 * Ce repository est automatiquement configure par Spring Data JPA
 * grace a @EnableJpaRepositories dans ArchiveDataSourceConfig.
 *
 * Il utilise :
 * - L'EntityManagerFactory "archiveEntityManagerFactory"
 * - Le TransactionManager "archiveTransactionManager"
 */
@Repository
public interface ArchivedOrderRepository extends JpaRepository<ArchivedOrder, Long> {

    /**
     * Recherche par ID de commande originale
     */
    Optional<ArchivedOrder> findByOriginalOrderId(Long originalOrderId);

    /**
     * Recherche par numero de commande
     */
    Optional<ArchivedOrder> findByOrderNumber(String orderNumber);

    /**
     * Recherche les archives par date d'archivage
     */
    List<ArchivedOrder> findByArchivedDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate);

    /**
     * Recherche les archives par statut original
     */
    List<ArchivedOrder> findByStatus(String status);

    /**
     * Compte le nombre d'archives pour une periode
     */
    @Query("SELECT COUNT(a) FROM ArchivedOrder a WHERE a.archivedDate BETWEEN :start AND :end")
    Long countArchivesInPeriod(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Verifie si une commande est deja archivee
     */
    boolean existsByOriginalOrderId(Long originalOrderId);
}
