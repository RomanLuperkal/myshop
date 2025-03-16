package org.ivanov.myshop.cart.repository.impl;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.repository.CustomCartRepository;
import org.ivanov.myshop.cart_item.model.CartItems;
import org.ivanov.myshop.product.model.Product;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class CustomCartRepositoryImpl implements CustomCartRepository {
    private final R2dbcEntityTemplate entityTemplate;
    private final ReactiveTransactionManager transactionManager;
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

    @Override
    public Mono<Cart> findByUserIpAndStatus(String userIp, Status status) {
        return entityTemplate.getDatabaseClient()
                .sql(SELECT_CART_BY_USER_IP_AND_STATUS)
                .bind("userIp", userIp)
                .bind("status", status.toString())
                .fetch()
                .all() // Возвращает Flux<Map<String, Object>>
                .collectList() // Собираем все строки в список
                .map(this::mapToCart); // Преобразуем список строк в объект Ca
        }

    private Cart mapToCart(List<Map<String, Object>> rows) {
        if (rows.isEmpty()) {
            return null; // Если нет данных, возвращаем null
        }

        // Берем первую строку для маппинга основной корзины
        Map<String, Object> firstRow = rows.get(0);
        Cart cart = mapCart(firstRow);

        // Группируем данные по элементам корзины
        Set<CartItems> cartItems = new HashSet<>();
        for (Map<String, Object> row : rows) {
            CartItems item = mapCartItem(row);
            if (item != null) {
                item.setCartId(cart.getCartId());
                // Добавляем связанный продукт
                Product product = mapProduct(row);
                if (product != null) {
                    item.setProduct(product);
                    item.setProductId(product.getProductId());
                }
                cartItems.add(item);
            }
        }

        // Устанавливаем элементы корзины
        cart.setOrderedProducts(cartItems);
        return cart;
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
            return null; // Если нет связанных элементов
        }
        CartItems item = new CartItems();
        item.setCurtItemId(curtItemId);
        item.setCount((Integer) row.get("item_count"));
        return item;
    }

    private Product mapProduct(Map<String, Object> row) {
        Long productId = (Long) row.get("product_id");
        if (productId == null) {
            return null; // Если нет связанных продуктов
        }
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName((String) row.get("product_name"));
        ByteBuffer imageBuffer = (ByteBuffer) row.get("image");
        byte[] imageBytes = new byte[imageBuffer.remaining()];
        imageBuffer.get(imageBytes);
        product.setImage(imageBytes);
        product.setDescription((String) row.get("description"));
        product.setCount((Long) row.get("product_count"));
        product.setPrice(new BigDecimal(row.get("price").toString()));
        return product;
    }
    }