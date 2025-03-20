package org.ivanov.myshop.product.service;

import lombok.SneakyThrows;
import org.ivanov.myshop.product.ProductTestBase;
import org.ivanov.myshop.product.dto.ListProductDto;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.dto.UpdateProductDto;
import org.ivanov.myshop.product.mapper.ProductMapper;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class ProductServiceTest extends ProductTestBase {
    @Autowired
    private ProductService productService;
    @MockitoBean(reset = MockReset.AFTER)
    private ProductRepository productRepository;
    @MockitoSpyBean(reset = MockReset.AFTER)
    private ProductMapper productMapper;

    @Test
    @SneakyThrows
    void createProductTest() {
        ProductCreateDto exceptedProductCreateDto = getProductCreateDto();
        when(productRepository.save(any())).thenReturn(Mono.empty());

        StepVerifier.create( productService.createProduct(exceptedProductCreateDto)).verifyComplete();

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @SneakyThrows
    void getProductsWhenReturnListProductDtoTest() {
        Pageable pageable = PageRequest.of(0, 1);
        ListProductDto exceptedProductListDto = getListProductDto();
        Product exceptedProduct = getProduct();
        Page<Product> page = new PageImpl<>(List.of(exceptedProduct), pageable, exceptedProductListDto.content().size());
        when(productRepository.findProducts(null, pageable)).thenReturn(Flux.fromIterable(List.of(exceptedProduct)));
        when(productRepository.countByProductNameContainingIgnoreCase(null)).thenReturn(Mono.just(10L));

        StepVerifier.create(productService.getProducts(pageable, null))

                .assertNext(response -> {
                    assertEquals(response.content().getFirst().productId(), exceptedProduct.getProductId());
                    verify(productRepository, times(1)).findProducts(any(), eq(pageable));
                })
                .verifyComplete();


    }

    @Test
    @SneakyThrows
    void getProductsWhenReturnListShortProductDtoTest() {
        Product exceptedProduct = getProduct();
        List<Product> exceptedProductList = List.of(exceptedProduct);
        when(productRepository.findAll()).thenReturn(Flux.fromIterable(exceptedProductList));

        StepVerifier.create(productService.getProducts())

                .assertNext(response -> {
                    assertEquals(response.products().getFirst().productId(), exceptedProduct.getProductId());
                    verify(productRepository, times(1)).findAll();
                })
                .verifyComplete();
    }

    @Test
    @SneakyThrows
    void updateProductTest() {
        UpdateProductDto exceptedProductUpdateDto = getUpdateProductDto();
        Product exceptedProduct = getProduct();
        when(productRepository.findById(exceptedProduct.getProductId())).thenReturn(Mono.just(exceptedProduct));
        when(productRepository.save(any())).thenReturn(Mono.empty());

        StepVerifier.create(productService.updateProduct(exceptedProductUpdateDto)).verifyComplete();

        assertAll(
                () -> assertEquals(exceptedProduct.getPrice(), exceptedProductUpdateDto.getPrice()),
                () -> assertEquals(exceptedProduct.getCount(), exceptedProductUpdateDto.getCount()),
                () -> verify(productRepository, times(1)).findById(exceptedProduct.getProductId())
        );
    }

    @Test
    @SneakyThrows
    void deleteProductTest() {
        when(productRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(productService.deleteProduct(1L)).verifyComplete();

        verify(productRepository, times(1)).deleteById(1L);
    }
}
