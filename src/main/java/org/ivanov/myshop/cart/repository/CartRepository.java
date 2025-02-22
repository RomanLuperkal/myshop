package org.ivanov.myshop.cart.repository;

import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.model.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {
    Optional<Cart> findByUserIpAndStatus(String userIp, Status status);
}
