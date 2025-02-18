package org.ivanov.myshop.product.service;

import org.ivanov.myshop.product.dto.ProductCreateDto;

public interface ProductService {
    void createProduct(ProductCreateDto productCreateDto);
}
