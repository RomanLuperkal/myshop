package org.ivanov.myshop.cart_item.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.product.model.Product;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItems {
    @Id
    @Column(name = "curt_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long curtItemId;
    @ManyToOne
    @JoinColumn(name = "curt_id")
    private Cart cart;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer count;

}
