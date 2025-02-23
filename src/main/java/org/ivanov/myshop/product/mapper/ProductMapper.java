package org.ivanov.myshop.product.mapper;

import org.ivanov.myshop.product.dto.ListProductDto;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.dto.ProductResponseDto;
import org.ivanov.myshop.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "image", source = "image", qualifiedByName = "getBytesFromMultipartFile")
    Product productCreateDtoToProduct(ProductCreateDto dto);

    @Mapping(target = "image", source = "image", qualifiedByName = "bytesToBase64")
    ProductResponseDto productToProductResponseDto(Product product);

    @Mapping(target = "content", source = "products.content")
    @Mapping(target = "number", expression = "java(products.getNumber())")
    @Mapping(target = "totalPages", expression = "java(products.getTotalPages())")
    @Mapping(target = "first", expression = "java(products.isFirst())")
    @Mapping(target = "last", expression = "java(products.isLast())")
    ListProductDto mapToListProductDto(Page<Product> products);


    @Named("bytesToBase64")
    default String bytesToBase64(byte[] image) {
        if (image == null) {
            return null;
        }
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(image);
    }

    @Named("getBytesFromMultipartFile")
    default byte[] getBytesFromMultipartFile(MultipartFile multipartFile) throws IOException {
        return multipartFile.getBytes();
    }

    default List<ProductResponseDto> mapToProductResponseDtoList(List<Product> products) {
        return products.stream()
                .map(this::productToProductResponseDto)
                .toList();
    }
}
