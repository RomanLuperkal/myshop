package org.ivanov.myshop.cart.repository;

import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.model.Cart;
import reactor.core.publisher.Mono;

public interface CustomCartRepository {
    Mono<Cart> findByAccountIdAndStatus(Long accountId, Status status);

    Mono<Cart> getFullCartById(Long curtId);
}
