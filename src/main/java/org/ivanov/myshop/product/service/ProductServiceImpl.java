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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    /*@Override
    public void createProduct(ProductCreateDto productCreateDto) {
        Product product = productMapper.productCreateDtoToProduct(productCreateDto);
        productRepository.save(product);
    }*/

    @Override
    public Mono<ListProductDto> getProducts(Pageable pageable, String search) {
        /*Specification<Product> spec = (root, query, criteriaBuilder) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(root.get("productName"), "%" + search + "%");
        };
        Page<Product> p =  productRepository.findAll(spec, pageable);
        p.getTotalPages();
        return productMapper.mapToListProductDto(productRepository.findAll(spec, pageable));*/
        Mono<List<Product>> products = productRepository.findByProductNameContainingIgnoreCase(search, pageable.getPageSize(), pageable.getOffset())
                .collectList();
        Mono<Long> count = productRepository.countByProductNameContainingIgnoreCase(search);
        Mono.zip(products, count).map(tuple -> {
            Long count
            ListProductDto listProductDto = new ListProductDto(productMapper.mapToProductResponseDtoList(tuple.getT1()),
                    pageable.getPageNumber(), tuple.getT2(), )
        })
        return null;
    }

    /*@Override
    public ListShortProductDto getProducts() {
        return productMapper.mapToListShortProductDto(productRepository.findAll());
    }*/

    /*@Override
    @Transactional
    public void updateProduct(UpdateProductDto updateProductDto) {
        Product product = productRepository.findById(updateProductDto.getProductId())
                .orElseThrow(() -> new ProductException(HttpStatus.NOT_FOUND, "Товара с id=" + updateProductDto.getProductId() + " не существует"));

        productMapper.mapToProduct(product, updateProductDto);
    }*/

    /*@Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }*/
}
