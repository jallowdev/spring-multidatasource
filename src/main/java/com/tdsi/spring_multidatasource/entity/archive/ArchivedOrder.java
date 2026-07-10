package com.tdsi.spring_multidatasource.entity.archive;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ===================================================================
 * Entite Commande Archivee - Base ARCHIVE
 * ===================================================================
 * Represente une commande archivee (historisee).
 * Cette entite est stockee dans la base de donnees "archive".
 *
 * L'archivage permet de :
 * - Liberer l'espace dans la base operationnelle
 * - Conserver l'historique pour des raisons legales
 * - Ameliorer les performances des requetes operationnelles
 */
@Entity
@Table(name = "archived_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivedOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_order_id", nullable = false)
    private Long originalOrderId;

    @Column(name = "order_number", nullable = false, length = 50)
    private String orderNumber;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_email", length = 150)
    private String customerEmail;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "archived_date", nullable = false)
    private LocalDateTime archivedDate;

    @Column(name = "original_created_at")
    private LocalDateTime originalCreatedAt;

    @Column(name = "archive_reason", length = 255)
    private String archiveReason;
}
