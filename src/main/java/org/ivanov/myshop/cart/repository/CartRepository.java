package org.ivanov.myshop.cart.repository;

import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.proection.ConfirmCart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {
    @Query("""
            SELECT c
            FROM Cart c
            JOIN FETCH c.orderedProducts ci
            JOIN FETCH ci.product p
            WHERE c.userIp = :userIp AND c.status = :status
            """)
    Optional<Cart> findByUserIpAndStatus(@Param("userIp") String userIp, @Param("status") Status status);

    @Query("""
            SELECT c
            FROM Cart c
            JOIN FETCH c.orderedProducts ci
            JOIN FETCH ci.product p
            WHERE c.curtId = :curtId
            """)
    Optional<Cart> getFullCartById(@Param("curtId") Long curtId);

    @Query("""
            SELECT c.curtId as id, c.confirmedDate as  confirmedDate, sum(p.price * ci.count) as cartPrice
            FROM Cart c
            JOIN CartItems ci on  c.curtId = ci.cart.curtId
            JOIN Product p on p.productId = ci.product.productId
            WHERE c.userIp = :userIp
            group by c.confirmedDate, c.curtId
            """)
    List<ConfirmCart> getConfirmCartsByUserIp(@Param("userIp") String userIp);
}
