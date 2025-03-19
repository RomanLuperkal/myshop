package org.ivanov.myshop.cart.service;

import lombok.SneakyThrows;
import org.ivanov.myshop.cart.CartTestBase;
import org.ivanov.myshop.cart.dto.*;
import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.mapper.CartMapper;
import org.ivanov.myshop.cart.mapper.CartMapperImpl;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.proection.ConfirmCart;
import org.ivanov.myshop.cart.repository.CartRepository;
import org.ivanov.myshop.cart_item.repository.CartItemRepository;
import org.ivanov.myshop.handler.exception.ProductException;
import org.ivanov.myshop.product.mapper.ProductMapperImpl;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {CartServiceImpl.class, CartMapperImpl.class, ProductMapperImpl.class})
@ActiveProfiles("test")
public class CartServiceTest extends CartTestBase {
    @Autowired
    private CartService cartService;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private CartItemRepository cartItemRepository;

    @MockitoSpyBean(reset = MockReset.AFTER)
    private CartMapper cartMapper;

    @Test
    void addToCartTestWhenProductNotFound() {
        CreateCartDto exceptingCartDto  = getCreateCartDto();
        HttpStatus exceptingStatus = HttpStatus.NOT_FOUND;
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Mono.empty());
        when(cartRepository.findByUserIpAndStatus(getCart().getUserIp(), Status.CREATED)).thenReturn(Mono.just(getCart()));

        StepVerifier.create(cartService.addToCart(exceptingCartDto, USER_IP))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ProductException.class);
                    ProductException actualException = (ProductException) error;
                    assertAll(
                            () -> assertEquals(exceptingStatus, actualException.getStatus()),
                            () -> verify(productRepository, times(1)).findById(exceptingCartDto.productId()),
                            () -> verify(cartRepository, never()).save(any(Cart.class)) // Сохранение корзины не должно происходить
                    );
                })
                .verify();
    }

    @Test
    void addToCartTestWhenProductCountLessDto() {
        CreateCartDto exceptingCartDto = getCreateCartDto();
        Product exceptingProduct = getProduct();
        exceptingProduct.setCount((long) (exceptingCartDto.count() - 1));
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Mono.just(exceptingProduct));
        when(cartRepository.findByUserIpAndStatus(getCart().getUserIp(), Status.CREATED)).thenReturn(Mono.just(getCart()));

        StepVerifier.create(cartService.addToCart(exceptingCartDto, USER_IP))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ProductException.class);
                    ProductException actualException = (ProductException) error;
                    assertEquals(actualException.getStatus(), HttpStatus.CONFLICT);
                }).verify();

        verify(productRepository, times(1)).findById(exceptingCartDto.productId());
        verify(cartRepository, times(0)).save(any(Cart.class));
    }

   /* @Test
    void addToCartTestWhenProductExistsInCart() {
        CreateCartDto exceptingCartDto = getCreateCartDto();
        Product exceptingProduct = getProduct();
        Cart exceptingCart = getCart();
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Mono.just(exceptingProduct));
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(exceptingCart));

        StepVerifier.create(cartService.addToCart(exceptingCartDto, USER_IP))
                .assertNext(response -> {
                    assertTrue(response.message().contains(exceptingProduct.getProductName()));
                    assertTrue(response.message().contains(exceptingCartDto.count().toString()));
                })
                .verifyComplete();

        verify(productRepository, times(1)).findById(exceptingCartDto.productId());
        verify(cartRepository, times(1)).save(exceptingCart);
    }*/

    /*@Test
    void removeFromCartTest() {
        Product exceptingProduct = getProduct();
        DeleteCartDto exceptingCartDto = getDeleteCartDto();
        Cart exceptingCart = getCart();
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Mono.just(exceptingProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(exceptingCart));

        StepVerifier.create(cartService.removeFromCart(exceptingCartDto, USER_IP))
                .assertNext(response -> {
                    assertTrue(response.message().contains(exceptingProduct.getProductName()));
                    assertTrue(response.message().contains(exceptingCartDto.count().toString()));
                })
                .verifyComplete();

        verify(productRepository, times(1)).findById(exceptingCartDto.productId());
        verify(cartRepository, times(1)).findByUserIpAndStatus(USER_IP, Status.CREATED);
        verify(cartRepository, times(1)).save(exceptingCart);
    }*/

    /*@Test
    void getActualCartTest() {
        Cart exceptingCart = getCart();
        Product exceptingProduct = getProduct();
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(cartMapper.mapToActualCartResponseDto(anySet())).thenReturn(new ActualCartResponseDto(List.of(
                new CartItemDto(exceptingProduct.getProductId(), exceptingProduct.getProductName(), exceptingProduct.getPrice())
        )));

        StepVerifier.create(cartService.getActualCart(USER_IP))
                .assertNext(response -> {
                    assertEquals(exceptingProduct.getProductName(), response.cartItems().getFirst().productName());
                    assertEquals(exceptingProduct.getPrice(), response.cartItems().getFirst().price());
                    assertEquals(exceptingProduct.getProductId(), response.cartItems().getFirst().productId());
                })
                .verifyComplete();

        verify(cartRepository, times(1)).findByUserIpAndStatus(USER_IP, Status.CREATED);
    }*/

    /*@Test
    void confirmCartTest() {
        Cart exceptingCart = getCart();
        when(cartRepository.findByUserIpAndStatus(USER_IP, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(exceptingCart));

        StepVerifier.create(cartService.confirmCart(USER_IP))
                .assertNext(cartId -> {
                    assertEquals(exceptingCart.getCartId(), cartId);
                    assertNotNull(exceptingCart.getConfirmedDate());
                    assertEquals(Status.DONE, exceptingCart.getStatus());
                })
                .verifyComplete();

        verify(cartRepository, times(1)).findByUserIpAndStatus(USER_IP, Status.CREATED);
    }*/

    /*@Test
    void getConfirmCartListTest() {
        List<ConfirmCart> exceptedConfirmCarts = getConfirmCarts();
        when(cartRepository.getConfirmCartsByUserIp(USER_IP)).thenReturn(Flux.fromIterable(exceptedConfirmCarts));
        when(cartMapper.mapToConfirmCartDto(anyList())).thenReturn(new ListConfirmCartDto(exceptedConfirmCarts.stream()
                .map(cart -> new ConfirmCartDto(cart.getId(), cart.getCartPrice(), cart.getConfirmedDate()))
                .toList()));

        StepVerifier.create(cartService.getConfirmCartList(USER_IP))
                .assertNext(response -> {
                    assertEquals(exceptedConfirmCarts.getFirst().getId(), response.confirmCarts().getFirst().getId());
                    assertEquals(exceptedConfirmCarts.getFirst().getCartPrice(), response.confirmCarts().getFirst().getCartPrice());
                    assertNotNull(response.confirmCarts().getFirst().getConfirmedDate());
                })
                .verifyComplete();

        verify(cartRepository, times(1)).getConfirmCartsByUserIp(USER_IP);
    }*/
}
