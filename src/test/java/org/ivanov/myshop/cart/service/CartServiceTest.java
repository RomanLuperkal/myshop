package org.ivanov.myshop.cart.service;

import lombok.SneakyThrows;
import org.ivanov.myshop.cart.CartTestBase;
import org.ivanov.myshop.cart.dto.*;
import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.mapper.CartMapper;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.proection.ConfirmCart;
import org.ivanov.myshop.cart.repository.CartRepository;
import org.ivanov.myshop.handler.exception.ProductException;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class CartServiceTest extends CartTestBase {
    @Autowired
    private CartService cartService;

    @MockitoBean(reset = MockReset.AFTER)
    private CartRepository cartRepository;

    @MockitoBean(reset = MockReset.AFTER)
    private ProductRepository productRepository;

    @MockitoSpyBean(reset = MockReset.AFTER)
    private CartMapper cartMapper;

    @Test
    @SneakyThrows
    void addToCartTestWhenProductNotFound() {
        CreateCartDto exceptingCartDto  = getCreateCartDto();
        HttpStatus exceptingStatus = HttpStatus.NOT_FOUND;
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Optional.empty());

        ProductException actualException = assertThrows(ProductException.class, () -> cartService.addToCart(exceptingCartDto, USER_IP));

        assertAll(
                () -> assertEquals(exceptingStatus, actualException.getStatus()),
                () -> verify(productRepository, times(1)).findById(exceptingCartDto.productId()),
                () -> verify(cartRepository, times(0)).save(any(Cart.class))
        );
    }

    @Test
    @SneakyThrows
    void addToCartTestWhenProductCountLessDto() {
        CreateCartDto exceptingCartDto  = getCreateCartDto();
        Product exceptingProduct = getProduct();
        exceptingProduct.setCount((long) (exceptingCartDto.count() - 1));
        HttpStatus exceptingStatus = HttpStatus.CONFLICT;
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Optional.of(exceptingProduct));

        ProductException actualException = assertThrows(ProductException.class, () -> cartService.addToCart(exceptingCartDto, USER_IP));

        assertAll(
                () -> assertEquals(exceptingStatus, actualException.getStatus()),
                () -> verify(productRepository, times(1)).findById(exceptingCartDto.productId()),
                () -> verify(cartRepository, times(0)).save(any(Cart.class))
        );
    }

    @Test
    @SneakyThrows
    void addToCartTestWhenProductExistsInCart() {
        CreateCartDto exceptingCartDto  = getCreateCartDto();
        Product exceptingProduct = getProduct();
        Cart exceptingCart = getCart();
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Optional.of(exceptingProduct));
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Optional.of(exceptingCart));

        CartResponseDto actualCartDto = cartService.addToCart(exceptingCartDto, USER_IP);

        assertAll(
                () -> assertTrue(actualCartDto.message().contains(exceptingProduct.getProductName())),
                () -> assertTrue(actualCartDto.message().contains(exceptingCartDto.count().toString())),
                () -> verify(productRepository, times(1)).findById(exceptingCartDto.productId()),
                () -> verify(cartRepository, times(1)).save(exceptingCart)
        );
    }

    @Test
    @SneakyThrows
    void addToCartTestWhenProductNotExistsInCart() {
        CreateCartDto exceptingCartDto  = getCreateCartDto();
        Product exceptingProduct = getProduct();
        exceptingProduct.setProductId(2L);
        Cart exceptingCart = getCart();
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Optional.of(exceptingProduct));
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Optional.of(exceptingCart));

        CartResponseDto actualCartDto = cartService.addToCart(exceptingCartDto, USER_IP);

        assertAll(
                () -> assertTrue(actualCartDto.message().contains(exceptingProduct.getProductName())),
                () -> assertTrue(actualCartDto.message().contains(exceptingCartDto.count().toString())),
                () -> verify(productRepository, times(1)).findById(exceptingCartDto.productId()),
                () -> verify(cartRepository, times(1)).save(exceptingCart)
        );
    }

    @Test
    @SneakyThrows
    void addToCartTestWhenCartEmpty() {
        CreateCartDto exceptingCartDto  = getCreateCartDto();
        Product exceptingProduct = getProduct();
        Cart exceptingCart = getCart();
        exceptingCart.setOrderedProducts(new HashSet<>());
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Optional.of(exceptingProduct));
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Optional.of(exceptingCart));

        CartResponseDto actualCartDto = cartService.addToCart(exceptingCartDto, USER_IP);

        assertAll(
                () -> assertTrue(actualCartDto.message().contains(exceptingProduct.getProductName())),
                () -> assertTrue(actualCartDto.message().contains(exceptingCartDto.count().toString())),
                () -> verify(productRepository, times(1)).findById(exceptingCartDto.productId()),
                () -> verify(cartRepository, times(1)).save(exceptingCart)
        );
    }

    @Test
    @SneakyThrows
    void removeFromCartTest() {
        Product exceptingProduct = getProduct();
        DeleteCartDto exceptingCartDto  = getDeleteCartDto();
        Cart exceptingCart = getCart();
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Optional.of(exceptingCart));
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Optional.of(exceptingProduct));

        cartService.removeFromCart(exceptingCartDto, USER_IP);

        assertAll(
                () -> verify(productRepository, times(1)).findById(exceptingCartDto.productId()),
                () -> verify(cartRepository, times(1)).findByUserIpAndStatus(USER_IP, Status.CREATED),
                () -> verify(cartRepository, times(1)).save(exceptingCart)
        );
    }

    @Test
    @SneakyThrows
    void removeFromCartTestWhenDtoCountLessOrderedProducts() {
        Product exceptingProduct = getProduct();
        DeleteCartDto exceptingCartDto  = getDeleteCartDto();
        Cart exceptingCart = getCart();
        exceptingCart.getOrderedProducts().forEach(cartItems -> cartItems.setCount(10));
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Optional.of(exceptingCart));
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Optional.of(exceptingProduct));

        cartService.removeFromCart(exceptingCartDto, USER_IP);

        assertAll(
                () -> verify(cartRepository, times(1)).findByUserIpAndStatus(USER_IP, Status.CREATED),
                () -> verify(cartRepository, times(1)).save(exceptingCart)
        );
    }

    @Test
    @SneakyThrows
    void getActualCartTest() {
        Cart exceptingCart = getCart();
        Product exceptingProduct = getProduct();
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Optional.of(exceptingCart));

        ActualCartResponseDto actualCart = cartService.getActualCart(USER_IP);

        assertAll(
                () -> assertEquals(actualCart.cartItems().getFirst().productName(), exceptingProduct.getProductName()),
                () -> assertEquals(actualCart.cartItems().getFirst().price(), exceptingProduct.getPrice()),
                () -> assertEquals(actualCart.cartItems().getFirst().productId(), exceptingProduct.getProductId()),
                () -> verify(cartRepository, times(1)).findByUserIpAndStatus(USER_IP, Status.CREATED)
        );
    }

    @Test
    @SneakyThrows
    void deleteProductFromCartTest() {
        Product exceptingProduct = getProduct();
        Cart exceptingCart = getCart();
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Optional.of(exceptingCart));
        when(productRepository.findById(exceptingProduct.getProductId())).thenReturn(Optional.of(exceptingProduct));

        cartService.deleteProductFromCart(exceptingProduct.getProductId(), USER_IP);

        assertAll(
                () -> verify(productRepository, times(1)).findById(exceptingProduct.getProductId()),
                () -> verify(cartRepository, times(1)).findByUserIpAndStatus(USER_IP, Status.CREATED),
                () -> verify(cartRepository, times(1)).save(exceptingCart)
        );
    }

    @Test
    @SneakyThrows
    void getConfirmCartTest() {
        Cart exceptingCart = getCart();
        Product exceptingProduct = getProduct();
        exceptingCart.setStatus(Status.DONE);
        when(cartRepository.getFullCartById(exceptingCart.getCartId())).thenReturn(Optional.of(exceptingCart));

        ConfirmCartResponseDto actualConfirmCart = cartService.getConfirmCart(exceptingCart.getCartId());

        assertAll(
                () -> assertEquals(actualConfirmCart.purchasedProducts().getFirst().productName(), exceptingProduct.getProductName()),
                () -> assertEquals(actualConfirmCart.purchasedProducts().getFirst().price(), exceptingProduct.getPrice()),
                () -> verify(cartRepository, times(1)).getFullCartById(exceptingCart.getCartId())
        );
    }

    @Test
    @SneakyThrows
    void confirmCartTest() {
        Cart exceptingCart = getCart();
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Optional.of(exceptingCart));

        Long actualConfirmCartId = cartService.confirmCart(USER_IP);
        assertAll(
                () -> assertEquals(exceptingCart.getCartId(), actualConfirmCartId),
                () -> assertNotNull(exceptingCart.getConfirmedDate()),
                () -> assertEquals(exceptingCart.getStatus(), Status.DONE),
                () -> verify(cartRepository, times(1)).findByUserIpAndStatus(USER_IP, Status.CREATED)
        );
    }

    @Test
    @SneakyThrows
    void getConfirmCartListTest() {
        List<ConfirmCart> exceptedConfirmCarts = getConfirmCarts();
        when(cartRepository.getConfirmCartsByUserIp(USER_IP)).thenReturn(exceptedConfirmCarts);

        ListConfirmCartDto actualConfirmCartList = cartService.getConfirmCartList(USER_IP);

        assertAll(
                () -> assertEquals(actualConfirmCartList.confirmCarts().getFirst().getId(), exceptedConfirmCarts.getFirst().getId()),
                () -> assertEquals(actualConfirmCartList.confirmCarts().getFirst().getCartPrice(), exceptedConfirmCarts.getFirst().getCartPrice()),
                ()  -> assertNotNull(actualConfirmCartList.confirmCarts().getFirst().getConfirmedDate()),
                () -> verify(cartRepository, times(1)).getConfirmCartsByUserIp(USER_IP)
        );
    }
}
