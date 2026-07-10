package com.tdsi.spring_multidatasource.repository.operational;

import com.tdsi.spring_multidatasource.entity.operational.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Product - Base OPERATIONNELLE
 * Acces aux produits dans la base operationnelle.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategory(String category);

    List<Product> findByQuantityInStockLessThan(Integer threshold);
}
