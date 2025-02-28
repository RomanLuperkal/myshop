package org.ivanov.myshop.product.service;

import lombok.SneakyThrows;
import org.ivanov.myshop.product.ProductTestBase;
import org.ivanov.myshop.product.dto.ListProductDto;
import org.ivanov.myshop.product.dto.ListShortProductDto;
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

import java.util.List;
import java.util.Optional;

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

        productService.createProduct(exceptedProductCreateDto);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @SneakyThrows
    void getProductsWhenReturnListProductDtoTest() {
        Pageable pageable = PageRequest.of(0, 1);
        ListProductDto exceptedProductListDto = getListProductDto();
        Product exceptedProduct = getProduct();
        Page<Product> page = new PageImpl<>(List.of(exceptedProduct), pageable, exceptedProductListDto.content().size());
        when(productRepository.findAll(any(), eq(pageable))).thenReturn(page);

        ListProductDto actualProducts = productService.getProducts(pageable, null);

        assertEquals(actualProducts.content().getFirst().productId(), exceptedProduct.getProductId());
        verify(productRepository, times(1)).findAll(any(), eq(pageable));
    }

    @Test
    @SneakyThrows
    void getProductsWhenReturnListShortProductDtoTest() {
        Product exceptedProduct = getProduct();
        List<Product> exceptedProductList = List.of(exceptedProduct);
        when(productRepository.findAll()).thenReturn(exceptedProductList);

        ListShortProductDto actualProducts = productService.getProducts();

        assertEquals(actualProducts.products().getFirst().productId(), exceptedProduct.getProductId());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @SneakyThrows
    void updateProductTest() {
        UpdateProductDto exceptedProductUpdateDto = getUpdateProductDto();
        Product exceptedProduct = getProduct();
        when(productRepository.findById(exceptedProduct.getProductId())).thenReturn(Optional.of(exceptedProduct));

        productService.updateProduct(exceptedProductUpdateDto);

        assertAll(
                () -> assertEquals(exceptedProduct.getPrice(), exceptedProductUpdateDto.getPrice()),
                () -> assertEquals(exceptedProduct.getCount(), exceptedProductUpdateDto.getCount()),
                () -> verify(productRepository, times(1)).findById(exceptedProduct.getProductId())
        );
    }

    @Test
    @SneakyThrows
    void deleteProductTest() {
        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }
}
