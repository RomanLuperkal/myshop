package org.ivanov.myshop.product.service;

import org.ivanov.myshop.product.dto.ListProductDto;
import org.ivanov.myshop.product.dto.ListShortProductDto;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.dto.UpdateProductDto;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    void createProduct(ProductCreateDto productCreateDto);

    ListProductDto getProducts(Pageable pageable, String search);

    ListShortProductDto getProducts();

    void updateProduct(UpdateProductDto updateProductDto);

    void deleteProduct(Long id);
}
