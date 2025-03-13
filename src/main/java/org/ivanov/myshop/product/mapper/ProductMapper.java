package org.ivanov.myshop.product.mapper;

import org.ivanov.myshop.product.dto.*;
import org.ivanov.myshop.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.codec.multipart.Part;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "image", ignore = true)
    Product productCreateDtoToProduct(ProductCreateDto dto);

    @Mapping(target = "image", source = "image", qualifiedByName = "bytesToBase64")
    ProductResponseDto productToProductResponseDto(Product product);

    @Mapping(target = "content", source = "products.content")
    @Mapping(target = "number", expression = "java(products.getNumber())")
    @Mapping(target = "totalPages", expression = "java(products.getTotalPages())")
    @Mapping(target = "first", expression = "java(products.isFirst())")
    @Mapping(target = "last", expression = "java(products.isLast())")
    ListProductDto mapToListProductDto(Page<Product> products);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "image", ignore = true)
    void mapToProduct(@MappingTarget Product product, UpdateProductDto updateProduct);

    ProductShortResponseDto mapToProductShortResponseDto(Product product);

    List<ProductShortResponseDto> mapToProductShortResponseDto(Iterable<Product> products);

    default ListShortProductDto mapToListShortProductDto(Iterable<Product> products) {
        return new ListShortProductDto(mapToProductShortResponseDto(products));
    }


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

    default Mono<byte[]> getBytesFromPart(Part part) {
        return DataBufferUtils.join(part.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                });
    }
}
