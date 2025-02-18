package org.ivanov.myshop.product.dto;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record ProductCreateDto(MultipartFile image, String description, Long count, BigDecimal price) {
}
