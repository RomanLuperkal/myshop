package org.ivanov.myshop.cart_item.model;

import lombok.Getter;
import lombok.Setter;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.product.model.Product;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "cart_items")
@Getter
@Setter
public class CartItems {
    @Id
    @Column("curt_item_id")
    private Long curtItemId;
   // @ManyToOne
   // @JoinColumn(name = "curt_id")
    private Cart cart;
   // @ManyToOne
   // @JoinColumn(name = "product_id")
    private Product product;
    private Integer count;

}
