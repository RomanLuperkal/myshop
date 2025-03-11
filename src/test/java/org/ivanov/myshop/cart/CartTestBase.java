package org.ivanov.myshop.cart;

/*import org.ivanov.myshop.cart.dto.CreateCartDto;
import org.ivanov.myshop.cart.dto.DeleteCartDto;
import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.proection.ConfirmCart;
import org.ivanov.myshop.cart_item.model.CartItems;
import org.ivanov.myshop.product.model.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class CartTestBase {
    protected final String USER_IP = "127.0.0.1";
    protected CreateCartDto getCreateCartDto() {
        return new CreateCartDto(1L, 1);
    }

    protected Product getProduct() {
        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("test product name");
        product.setImage(new byte[1]);
        product.setDescription("test product description");
        product.setPrice(new BigDecimal("1.5"));
        product.setCount(10L);
        return product;
    }

    protected CartItems getCartItems() {
        CartItems cartItems = new CartItems();
        cartItems.setCurtItemId(1L);
        cartItems.setProduct(getProduct());
        cartItems.setCount(1);
        return cartItems;
    }

    protected Cart getCart() {
        Cart cart = new Cart();
        CartItems cartItems = getCartItems();
        cartItems.setCart(cart);
        cart.setCartId(1L);
        cart.setStatus(Status.CREATED);
        cart.setUserIp(USER_IP);
        cart.setCreationDate(LocalDateTime.now());
        cart.setOrderedProducts(new HashSet<>(List.of(cartItems)));
        cart.setConfirmedDate(LocalDateTime.now().plusHours(1));
        return cart;
    }

    protected DeleteCartDto getDeleteCartDto() {
        return new DeleteCartDto(1L, 1);
    }

    protected List<ConfirmCart> getConfirmCarts() {
        List<ConfirmCart> confirmCarts = new ArrayList<>();
        ConfirmCart confirmCart = new ConfirmCart() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public LocalDateTime getConfirmedDate() {
                return LocalDateTime.now();
            }

            @Override
            public BigDecimal getCartPrice() {
                return new BigDecimal("1.5");
            }
        };
        confirmCarts.add(confirmCart);
        return confirmCarts;
    }
}*/
