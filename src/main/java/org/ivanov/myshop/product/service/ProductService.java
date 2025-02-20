package org.ivanov.myshop.product.service;

import org.ivanov.myshop.product.dto.ListProductDto;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    void createProduct(ProductCreateDto productCreateDto);

    ListProductDto getProducts(Pageable pageable, String search);
}
