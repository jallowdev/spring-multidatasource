package com.tdsi.spring_multidatasource.config;


import com.tdsi.spring_multidatasource.entity.operational.Order;
import com.tdsi.spring_multidatasource.entity.operational.Product;
import com.tdsi.spring_multidatasource.repository.operational.OrderRepository;
import com.tdsi.spring_multidatasource.repository.operational.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * ===================================================================
 * Initialisateur de Donnees de Test
 * ===================================================================
 * Cree des donnees de test au demarrage de l'application
 * si les tables sont vides.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initialisation des donnees de test...");

        // Creer des produits si vide
        if (productRepository.count() == 0) {
            log.info("Creation des produits de test...");
            createSampleProducts();
        }

        // Creer des commandes si vide
        if (orderRepository.count() == 0) {
            log.info("Creation des commandes de test...");
            createSampleOrders();
        }

        log.info("Initialisation terminee.");
    }

    private void createSampleProducts() {
        Product[] products = {
                Product.builder()
                        .sku("LAPTOP-001")
                        .name("Ordinateur Portable Pro")
                        .description("Ordinateur portable haute performance pour developpeurs")
                        .price(new BigDecimal("1299.99"))
                        .quantityInStock(50)
                        .category("Informatique")
                        .build(),
                Product.builder()
                        .sku("MOUSE-001")
                        .name("Souris Sans Fil Ergonomique")
                        .description("Souris ergonomique avec capteur haute precision")
                        .price(new BigDecimal("49.99"))
                        .quantityInStock(200)
                        .category("Peripheriques")
                        .build(),
                Product.builder()
                        .sku("KEYB-001")
                        .name("Clavier Mecanique RGB")
                        .description("Clavier mecanique avec switches Cherry MX Red")
                        .price(new BigDecimal("129.99"))
                        .quantityInStock(75)
                        .category("Peripheriques")
                        .build(),
                Product.builder()
                        .sku("MON-001")
                        .name("Ecran 27\" 4K IPS")
                        .description("Ecran 27 pouces 4K avec dalle IPS et calibration usine")
                        .price(new BigDecimal("499.99"))
                        .quantityInStock(30)
                        .category("Ecrans")
                        .build()
        };

        productRepository.saveAll(Arrays.asList(products));
        log.info("{} produits crees", products.length);
    }

    private void createSampleOrders() {
        // Commandes recentes (a garder operationnelles)
        Order[] recentOrders = {
                Order.builder()
                        .orderNumber("ORD-2024-001")
                        .customerName("Jean Dupont")
                        .customerEmail("jean.dupont@email.com")
                        .totalAmount(new BigDecimal("1349.98"))
                        .status(Order.OrderStatus.DELIVERED)
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .build(),
                Order.builder()
                        .orderNumber("ORD-2024-002")
                        .customerName("Marie Martin")
                        .customerEmail("marie.martin@email.com")
                        .totalAmount(new BigDecimal("49.99"))
                        .status(Order.OrderStatus.PENDING)
                        .createdAt(LocalDateTime.now().minusDays(2))
                        .build(),
                Order.builder()
                        .orderNumber("ORD-2024-003")
                        .customerName("Pierre Bernard")
                        .customerEmail("pierre.bernard@email.com")
                        .totalAmount(new BigDecimal("1629.97"))
                        .status(Order.OrderStatus.CONFIRMED)
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build()
        };

        // Commandes anciennes (candidates a l'archivage)
        Order[] oldOrders = {
                Order.builder()
                        .orderNumber("ORD-2023-001")
                        .customerName("Sophie Petit")
                        .customerEmail("sophie.petit@email.com")
                        .totalAmount(new BigDecimal("499.99"))
                        .status(Order.OrderStatus.DELIVERED)
                        .createdAt(LocalDateTime.now().minusDays(60))
                        .build(),
                Order.builder()
                        .orderNumber("ORD-2023-002")
                        .customerName("Lucas Moreau")
                        .customerEmail("lucas.moreau@email.com")
                        .totalAmount(new BigDecimal("179.98"))
                        .status(Order.OrderStatus.CANCELLED)
                        .createdAt(LocalDateTime.now().minusDays(90))
                        .build(),
                Order.builder()
                        .orderNumber("ORD-2023-003")
                        .customerName("Emma Richard")
                        .customerEmail("emma.richard@email.com")
                        .totalAmount(new BigDecimal("1299.99"))
                        .status(Order.OrderStatus.DELIVERED)
                        .createdAt(LocalDateTime.now().minusDays(120))
                        .build()
        };

        orderRepository.saveAll(Arrays.asList(recentOrders));
        orderRepository.saveAll(Arrays.asList(oldOrders));
        log.info("{} commandes recentes + {} commandes anciennes creees",
                recentOrders.length, oldOrders.length);
    }
}
