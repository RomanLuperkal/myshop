package org.ivanov.myshop.product;

import lombok.SneakyThrows;
import org.ivanov.myshop.product.dto.ListProductDto;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.dto.ProductResponseDto;
import org.ivanov.myshop.product.dto.UpdateProductDto;
import org.ivanov.myshop.product.mapper.ProductMapper;
import org.ivanov.myshop.product.model.Product;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Flux;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

public abstract class ProductTestBase {
    private final static Part part = createMockPart("test_image.jpg");

    @Autowired
    private ProductMapper productMapper;

    protected ProductCreateDto getProductCreateDto() {
        return new ProductCreateDto("testProductName", part,
                "testDescription", 10L, new BigDecimal("1.5"));
    }

    @SneakyThrows
    protected Product getProduct() {
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("test product name");
        product.setImage(productMapper.getBytesFromPart(part).block());
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
        updateProductDto.setImage(part);
        return updateProductDto;
    }

    @SneakyThrows
    public static Part createMockPart(String resourcePath) {
        File file = ResourceUtils.getFile("classpath:" + resourcePath);
        byte[] fileContent = Files.readAllBytes(file.toPath());

        Part mockPart = Mockito.mock(Part.class);

        Mockito.when(mockPart.name()).thenReturn("image");
        Mockito.when(mockPart.content()).thenReturn(Flux.just(toDataBuffer(fileContent)));

        return mockPart;
}

    private static DataBuffer toDataBuffer(byte[] data) {
        DefaultDataBufferFactory factory = new DefaultDataBufferFactory();
        DataBuffer buffer = factory.allocateBuffer(data.length);
        buffer.write(data);
        return buffer;
    }
}
