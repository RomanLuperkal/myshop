package org.ivanov.myshop.product.service;

import org.ivanov.myshop.product.dto.*;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<Void> createProduct(ProductCreateDto productCreateDto);

    Mono<ListProductDto> getProducts(Pageable pageable, String search);

    Mono<ListShortProductDto> getProducts();

    Mono<Void> updateProduct(UpdateProductDto updateProductDto);

    Mono<Void> deleteProduct(Long id);

    Mono<ProductResponseDto> getProduct(Long id);
}
