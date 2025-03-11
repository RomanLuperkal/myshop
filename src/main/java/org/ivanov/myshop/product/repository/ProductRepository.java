package org.ivanov.myshop.product.repository;

import org.ivanov.myshop.product.model.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends R2dbcRepository<Product, Long> {
    @Query("""
            SELECT * FROM Product p
            WHERE :productName IS NULL
            OR :productName = ''
            OR product_name LIKE LOWER('%' || :productName || '%')
            LIMIT :size OFFSET :offset
            """)
    Flux<Product> findByProductNameContainingIgnoreCase(@Param("productName") String productName, @Param("size") Integer size, @Param("offset") Long offset);

    @Query("""
            SELECT count(*) FROM Product p
            WHERE :productName IS NULL
            OR :productName = ''
            OR product_name LIKE LOWER('%' || :productName || '%')
            """)
    Mono<Long> countByProductNameContainingIgnoreCase(@Param("productName") String productName);
}
