package org.ivanov.myshop.product.service;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.product.dto.ListProductDto;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.mapper.ProductMapper;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.ProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
}
