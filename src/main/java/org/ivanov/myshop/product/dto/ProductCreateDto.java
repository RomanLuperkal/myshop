package org.ivanov.myshop.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import org.springframework.http.codec.multipart.Part;

import java.math.BigDecimal;

public record ProductCreateDto(
                               @NotBlank(message = "Имя товара не может быть пустым")
                               String productName,
                               Part image,
                               @NotBlank(message = "Описание товара не может быть пустым")
                               String description,
                               @Positive(message = "Некорректное кол-во товара")
                               Long count,
                               @Positive(message = "Некорректная цена")
                               BigDecimal price) {
}
