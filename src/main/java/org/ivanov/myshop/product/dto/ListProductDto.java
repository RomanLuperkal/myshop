package org.ivanov.myshop.product.dto;

import java.util.List;

public record ListProductDto(
        List<ProductResponseDto> content,
        Integer number,
        Integer totalPages,
        boolean first,
        boolean last
) {}
