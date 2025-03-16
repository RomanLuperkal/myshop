package org.ivanov.myshop.cart_item.repository;

import org.ivanov.myshop.cart_item.model.CartItems;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends R2dbcRepository<CartItems, Long> {
}
