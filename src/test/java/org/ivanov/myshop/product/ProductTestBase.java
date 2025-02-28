package org.ivanov.myshop.product;

import lombok.SneakyThrows;
import org.ivanov.myshop.product.dto.ListProductDto;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.dto.ProductResponseDto;
import org.ivanov.myshop.product.dto.UpdateProductDto;
import org.ivanov.myshop.product.mapper.ProductMapper;
import org.ivanov.myshop.product.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

public abstract class ProductTestBase {
    private final static MockMultipartFile multipartFile = createMultipartFile("test_image.jpg");

    @Autowired
    private ProductMapper productMapper;

    protected ProductCreateDto getProductCreateDto() {
        return new ProductCreateDto("testProductName", multipartFile,
                "testDescription", 10L, new BigDecimal("1.5"));
    }

    @SneakyThrows
    protected Product getProduct() {
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("test product name");
        product.setImage(multipartFile.getBytes());
        product.setDescription("test product description");
        product.setPrice(new BigDecimal("1.5"));
        product.setCount(10L);
        return product;
    }

    protected ProductResponseDto getProductResponseDto() {
        return productMapper.productToProductResponseDto(getProduct());
    }

    protected ListProductDto getListProductDto() {
        return new ListProductDto(new ArrayList<>(Collections.singletonList(getProductResponseDto())),
                0, 0, true, false);
    }

    protected UpdateProductDto getUpdateProductDto() {
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setProductId(1L);
        updateProductDto.setProductName("test product name");
        updateProductDto.setDescription("test product description");
        updateProductDto.setPrice(new BigDecimal("2.5"));
        updateProductDto.setCount(20L);
        updateProductDto.setImage(multipartFile);
        return updateProductDto;
    }

    @SneakyThrows
    private static MockMultipartFile createMultipartFile(String resourcePath) {
        var file = ResourceUtils.getFile("classpath:" + resourcePath);
        var fileContent = Files.readAllBytes(file.toPath());

        return new MockMultipartFile("image", file.getName(), "image/jpeg", fileContent);
    }
}
