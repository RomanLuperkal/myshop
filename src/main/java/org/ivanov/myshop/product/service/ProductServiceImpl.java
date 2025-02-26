package org.ivanov.myshop.product.service;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.handler.exception.ProductException;
import org.ivanov.myshop.product.dto.ListProductDto;
import org.ivanov.myshop.product.dto.ListShortProductDto;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.dto.UpdateProductDto;
import org.ivanov.myshop.product.mapper.ProductMapper;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.ProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public void createProduct(ProductCreateDto productCreateDto) {
        Product product = productMapper.productCreateDtoToProduct(productCreateDto);
        productRepository.save(product);
    }

    @Override
    public ListProductDto getProducts(Pageable pageable, String search) {
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(root.get("productName"), "%" + search + "%");
        };
        return productMapper.mapToListProductDto(productRepository.findAll(spec, pageable));
    }

    @Override
    public ListShortProductDto getProducts() {
        return productMapper.mapToListShortProductDto(productRepository.findAll());
    }

    @Override
    @Transactional
    public void updateProduct(UpdateProductDto updateProductDto) {
        Product product = productRepository.findById(updateProductDto.getProductId())
                .orElseThrow(() -> new ProductException(HttpStatus.NOT_FOUND, "Товара с id=" + updateProductDto.getProductId() + " не существует"));

        productMapper.mapToProduct(product, updateProductDto);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
