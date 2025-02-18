package org.ivanov.myshop.product.mapper;

import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "image", source = "image", qualifiedByName = "getBytesFromMultipartFile")
    Product productCreateDtoToProduct(ProductCreateDto dto);

    @Named("getBytesFromMultipartFile")
    default byte[] getBytesFromMultipartFile(MultipartFile multipartFile) throws IOException {
        return multipartFile.getBytes();
    }
}
