package org.ivanov.myshop.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class UpdateProductDto {
    private Long productId;
    @NotBlank(message = "Имя товара не может быть пустым")
    private String productName;
    private MultipartFile image;
    @NotBlank(message = "Описание товара не может быть пустым")
    private String description;
    @Positive(message = "Некорректное кол-во товара")
    private Long count;
    @Positive(message = "Некорректная цена")
    private BigDecimal price;
}
