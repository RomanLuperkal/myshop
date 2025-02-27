package org.ivanov.myshop.cart.service;

import lombok.SneakyThrows;
import org.ivanov.myshop.cart.CartTestBase;
import org.ivanov.myshop.cart.dto.CartResponseDto;
import org.ivanov.myshop.cart.dto.CreateCartDto;
import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.mapper.CartMapper;
import org.ivanov.myshop.cart.model.Cart;
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

import java.util.HashSet;
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

    @MockitoBean(reset = MockReset.AFTER)
    private CartMapper cartMapper;

    @Test
    @SneakyThrows
    void addToCartTestWhenProductNotFound() {
        CreateCartDto exceptingCartDto  = getCreateCartDto();
        HttpStatus exceptingStatus = HttpStatus.NOT_FOUND;
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Optional.empty());

        ProductException actualException = assertThrows(ProductException.class, () -> cartService.addToCart(exceptingCartDto, USER_IP));

        assertEquals(exceptingStatus, actualException.getStatus());
        verify(productRepository, times(1)).findById(exceptingCartDto.productId());
        verify(cartRepository, times(0)).save(any(Cart.class));
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

        assertEquals(exceptingStatus, actualException.getStatus());
        verify(productRepository, times(1)).findById(exceptingCartDto.productId());
        verify(cartRepository, times(0)).save(any(Cart.class));
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

        assertTrue(actualCartDto.message().contains(exceptingProduct.getProductName()));
        assertTrue(actualCartDto.message().contains(exceptingCartDto.count().toString()));

        verify(productRepository, times(1)).findById(exceptingCartDto.productId());
        verify(cartRepository, times(1)).save(exceptingCart);
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

        assertTrue(actualCartDto.message().contains(exceptingProduct.getProductName()));
        assertTrue(actualCartDto.message().contains(exceptingCartDto.count().toString()));

        verify(productRepository, times(1)).findById(exceptingCartDto.productId());
        verify(cartRepository, times(1)).save(exceptingCart);
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

        assertTrue(actualCartDto.message().contains(exceptingProduct.getProductName()));
        assertTrue(actualCartDto.message().contains(exceptingCartDto.count().toString()));

        verify(productRepository, times(1)).findById(exceptingCartDto.productId());
        verify(cartRepository, times(1)).save(exceptingCart);
    }
}
