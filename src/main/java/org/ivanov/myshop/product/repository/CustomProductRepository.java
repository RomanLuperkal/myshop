package org.ivanov.myshop.product.repository;

import org.ivanov.myshop.product.model.Product;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

public interface CustomProductRepository {
    Flux<Product> findProducts(String search, Pageable pageable);
}
