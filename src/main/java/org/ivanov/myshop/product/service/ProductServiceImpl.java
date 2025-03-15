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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Mono<Void> createProduct(ProductCreateDto productCreateDto) {
        Product product = productMapper.productCreateDtoToProduct(productCreateDto);
        Mono<byte[]> image = productMapper.getBytesFromPart(productCreateDto.image());
        return image.doOnNext(product::setImage).then(Mono.defer(() -> productRepository.save(product).then()));
    }

    @Override
    public Mono<ListProductDto> getProducts(Pageable pageable, String search) {
        Mono<List<Product>> products = productRepository.findProducts(search, pageable).collectList();
        Mono<Long> count = productRepository.countByProductNameContainingIgnoreCase(search);
        return Mono.zip(products, count).map(tuple -> {
            BigDecimal totalElements = BigDecimal.valueOf(tuple.getT2());
            BigDecimal pageSize = BigDecimal.valueOf(pageable.getPageSize());
            BigDecimal totalPages = totalElements.divide(pageSize, RoundingMode.CEILING);
            Boolean isFirst = pageable.getPageNumber() == 0;
            Boolean isLast = pageable.getPageNumber() + 1 == totalPages.intValue();
            return new ListProductDto(productMapper.mapToProductResponseDtoList(tuple.getT1()),
                    pageable.getPageNumber(), totalPages.intValue(), isFirst, isLast);
        });
    }

    @Override
    public Mono<ListShortProductDto> getProducts() {
        return productRepository.findAll().collectList().map(productMapper::mapToListShortProductDto);
    }

    @Override
    public Mono<Void> updateProduct(UpdateProductDto updateProductDto) {
        return productRepository.findById(updateProductDto.getProductId())
                .switchIfEmpty(Mono.error(new ProductException(HttpStatus.NOT_FOUND, "Товара с id=" + updateProductDto.getProductId() + " не существует")))
                .flatMap(existingProduct -> {
                    productMapper.mapToProduct(existingProduct, updateProductDto);

                    return productMapper.getBytesFromPart(updateProductDto.getImage())
                            .doOnNext(existingProduct::setImage)
                            .then(productRepository.save(existingProduct));
                })
                .then();
    }

    @Override
    public Mono<Void> deleteProduct(Long id) {
        return productRepository.deleteById(id);
    }

}
