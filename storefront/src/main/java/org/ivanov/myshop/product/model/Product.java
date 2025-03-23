package org.ivanov.myshop.product.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
public class Product {

    @Id
    @Column("product_id")
    private Long productId;
    @Column("product_name")
    private String productName;
    private byte[] image;
    private String description;
    private Long count;
    private BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(productId);
    }
}
