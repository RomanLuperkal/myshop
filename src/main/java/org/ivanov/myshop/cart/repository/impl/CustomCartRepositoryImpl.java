package org.ivanov.myshop.cart.repository.impl;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.repository.CustomCartRepository;
import org.ivanov.myshop.cart_item.model.CartItems;
import org.ivanov.myshop.product.model.Product;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CustomCartRepositoryImpl implements CustomCartRepository {
    private final R2dbcEntityTemplate entityTemplate;
    private final String SELECT_CART_BY_USER_IP_AND_STATUS = """
            SELECT c.curt_id,
                   c.user_ip,
                   c.creation_date,
                   c.confirmed_date,
                   c.status,
                   ci.curt_item_id,
                   ci.count AS item_count,
                   p.product_id,
                   p.product_name,
                   p.image,
                   p.description,
                   p.count  AS product_count,
                   p.price
            FROM cart c
                     LEFT JOIN cart_items ci ON c.curt_id = ci.curt_id
                     LEFT JOIN product p ON ci.product_id = p.product_id
            WHERE c.user_ip = :userIp
              AND c.status = :status
            """;

    private final String SELECT_FULL_CART = """
            SELECT *
            FROM cart c
            JOIN cart_items ci ON ci.curt_id = c.curt_id
            JOIN product p ON p.product_id = ci.product_id
            WHERE c.curt_id = :curtId
            """;

    @Override
    public Mono<Cart> findByUserIpAndStatus(String userIp, Status status) {
        return entityTemplate.getDatabaseClient()
                .sql(SELECT_CART_BY_USER_IP_AND_STATUS)
                .bind("userIp", userIp)
                .bind("status", status.toString())
                .fetch()
                .all()
                .collectList()
                .flatMap(this::mapToCart);
    }

    @Override
    public Mono<Cart> getFullCartById(Long curtId) {
        return entityTemplate.getDatabaseClient()
                .sql(SELECT_FULL_CART)
                .bind("curtId", curtId)
                .fetch()
                .all()
                .collectList()
                .flatMap(this::mapToCart);
    }

    private Mono<Cart> mapToCart(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return Mono.empty();
        }

        Map<String, Object> firstRow = rows.get(0);
        Cart cart = mapCart(firstRow);

        Set<CartItems> cartItems = new HashSet<>();
        for (Map<String, Object> row : rows) {
            CartItems item = mapCartItem(row);
            if (item != null) {
                item.setCartId(cart.getCartId());
                Product product = mapProduct(row);
                if (product != null) {
                    item.setProduct(product);
                    item.setProductId(product.getProductId());
                }
                cartItems.add(item);
            }
        }

        cart.setOrderedProducts(cartItems);
        return Mono.just(cart);
    }

    private Cart mapCart(Map<String, Object> row) {
        Cart cart = new Cart();
        cart.setCartId((Long) row.get("curt_id"));
        cart.setUserIp((String) row.get("user_ip"));
        cart.setCreationDate((LocalDateTime) row.get("creation_date"));
        cart.setConfirmedDate((LocalDateTime) row.get("confirmed_date"));
        cart.setStatus(Status.valueOf((String) row.get("status")));
        return cart;
    }

    private CartItems mapCartItem(Map<String, Object> row) {
        Long curtItemId = (Long) row.get("curt_item_id");
        if (curtItemId == null) {
            return null;
        }
        CartItems item = new CartItems();
        item.setCurtItemId(curtItemId);
        item.setCount((Integer) row.get("item_count"));
        return item;
    }

    private Product mapProduct(Map<String, Object> row) {
        Long productId = (Long) row.get("product_id");
        if (productId == null) {
            return null;
        }
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName((String) row.get("product_name"));
        byte[] imageBytes = (byte[]) row.get("image");
        //byte[] imageBytes = new byte[imageBuffer.remaining()];
        //imageBuffer.get(imageBytes);
        product.setImage(imageBytes);
        product.setDescription((String) row.get("description"));
        product.setCount((Long) row.get("product_count"));
        product.setPrice(new BigDecimal(row.get("price").toString()));
        return product;
    }
}