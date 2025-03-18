package org.ivanov.myshop.cart.repository;

import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.proection.ConfirmCart;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CartRepository extends R2dbcRepository<Cart, Long>, CustomCartRepository {
    @Query("""
            SELECT c.curt_id as id, c.confirmed_date, sum(p.price * ci.count) as cart_price
            FROM cart c
            JOIN cart_items ci on  c.curt_id = ci.curt_id
            JOIN product p on p.product_id = ci.product_id
            WHERE c.user_ip = :userIp
            AND c.status = 'DONE'
            group by c.confirmed_date, c.curt_id
            """)
    Flux<ConfirmCart> getConfirmCartsByUserIp(@Param("userIp") String userIp);
}
