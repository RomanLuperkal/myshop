package org.ivanov.myshop.cart.repository;

import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.model.Cart;
import reactor.core.publisher.Mono;

public interface CustomCartRepository {
    Mono<Cart> findByUserIpAndStatus(String userIp,Status status);

    Mono<Cart> getFullCartById(Long curtId);
}
