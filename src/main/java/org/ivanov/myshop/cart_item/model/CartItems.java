package org.ivanov.myshop.cart_item.model;

import lombok.Getter;
import lombok.Setter;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.product.model.Product;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "cart_items")
@Getter
@Setter
public class CartItems {
    @Id
    @Column("curt_item_id")
    private Long curtItemId;
   @Column("curt_id")
    private Long cartId;
    @Transient
    private Cart cart;
    @Column("product_id")
    private Long productId;
    @Transient
    private Product product;
    private Integer count;

}
