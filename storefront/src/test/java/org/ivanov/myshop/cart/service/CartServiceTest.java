package org.ivanov.myshop.cart.service;

import lombok.SneakyThrows;
import org.ivanov.myshop.account.client.AccountServiceClient;
import org.ivanov.myshop.account.dto.BalanceResponseDto;
import org.ivanov.myshop.account.dto.ProcessPaymentDto;
import org.ivanov.myshop.cart.CartTestBase;
import org.ivanov.myshop.cart.dto.CreateCartDto;
import org.ivanov.myshop.cart.dto.DeleteCartDto;
import org.ivanov.myshop.cart.dto.ListConfirmCartDto;
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
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

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
    private AccountServiceClient accountService;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private CartItemRepository cartItemRepository;

    @MockitoSpyBean(reset = MockReset.AFTER)
    private CartMapper cartMapper;

    @MockitoBean
    private WebSession session;

    @Test
    void addToCartTestWhenProductNotFound() {
        CreateCartDto exceptingCartDto  = getCreateCartDto();
        HttpStatus exceptingStatus = HttpStatus.NOT_FOUND;
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Mono.empty());
        when(cartRepository.findByAccountIdAndStatus(getCart().getAccountId(), Status.CREATED)).thenReturn(Mono.just(getCart()));

        StepVerifier.create(cartService.addToCart(exceptingCartDto, USER_ID, session))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ProductException.class);
                    ProductException actualException = (ProductException) error;
                    assertAll(
                            () -> assertEquals(exceptingStatus, actualException.getStatus()),
                            () -> verify(productRepository, times(1)).findById(exceptingCartDto.productId()),
                            () -> verify(cartRepository, never()).save(any(Cart.class))
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
        when(cartRepository.findByAccountIdAndStatus(getCart().getAccountId(), Status.CREATED)).thenReturn(Mono.just(getCart()));

        StepVerifier.create(cartService.addToCart(exceptingCartDto, USER_ID, session))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ProductException.class);
                    ProductException actualException = (ProductException) error;
                    assertEquals(actualException.getStatus(), HttpStatus.CONFLICT);
                }).verify();

        verify(productRepository, times(1)).findById(exceptingCartDto.productId());
        verify(cartRepository, times(0)).save(any(Cart.class));
    }

    @Test
    void addToCartTestWhenProductExistsInCart() {
        CreateCartDto exceptingCartDto = getCreateCartDto();
        Product exceptingProduct = getProduct();
        Cart exceptingCart = getCart();

        //Context context = getContext();
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Mono.just(exceptingProduct));
        when(cartRepository.findByAccountIdAndStatus(USER_ID, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(exceptingCart));
        when(cartItemRepository.saveAll(anyIterable())).thenReturn(Flux.fromIterable(exceptingCart.getOrderedProducts()));

        StepVerifier.create(
                        cartService.addToCart(exceptingCartDto, USER_ID, session)
                )
                .assertNext(response -> {
                    assertTrue(response.message().contains(exceptingProduct.getProductName()));
                    assertTrue(response.message().contains(exceptingCartDto.count().toString()));
                })
                .verifyComplete();

        verify(productRepository, times(1)).findById(exceptingCartDto.productId());
        verify(cartRepository, times(1)).save(exceptingCart);
    }

    @Test
    void removeFromCartTest() {
        Product exceptingProduct = getProduct();
        DeleteCartDto exceptingCartDto = getDeleteCartDto();
        Cart exceptingCart = getCart();
        when(cartRepository.findByAccountIdAndStatus(USER_ID, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Mono.just(exceptingProduct));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(exceptingCart));
        when(cartItemRepository.delete(any())).thenReturn(Mono.empty());

        StepVerifier.create(cartService.removeFromCart(exceptingCartDto, USER_ID, session))
                .assertNext(response -> {
                    assertTrue(response.message().contains(exceptingProduct.getProductName()));
                    assertTrue(response.message().contains(exceptingCartDto.count().toString()));
                })
                .verifyComplete();

        verify(productRepository, times(1)).findById(exceptingCartDto.productId());
        verify(cartRepository, times(1)).findByAccountIdAndStatus(USER_ID, Status.CREATED);
        verify(cartItemRepository, times(1)).delete(any());
    }

    @Test
    void getActualCartTest() {
        Cart exceptingCart = getCart();
        Product exceptingProduct = getProduct();
        BalanceResponseDto balanceResponseDto = new BalanceResponseDto();
        balanceResponseDto.setBalance(new BigDecimal(100));
        when(cartRepository.findByAccountIdAndStatus(USER_ID, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(accountService.getBalance(USER_ID, session)).thenReturn(Mono.just(balanceResponseDto));

        StepVerifier.create(cartService.getActualCart(USER_ID, session))
                .assertNext(response -> {
                    assertEquals(exceptingProduct.getProductName(), response.cartItems().getFirst().productName());
                    assertEquals(exceptingProduct.getPrice(), response.cartItems().getFirst().price());
                    assertEquals(exceptingProduct.getProductId(), response.cartItems().getFirst().productId());
                })
                .verifyComplete();

        verify(cartRepository, times(1)).findByAccountIdAndStatus(USER_ID, Status.CREATED);
    }

    @Test
    void confirmCartTest() {
        Cart exceptingCart = getCart();
        ProcessPaymentDto processPaymentDto = new ProcessPaymentDto();
        processPaymentDto.setAccountId(USER_ID);
        processPaymentDto.setOrderSum(new BigDecimal(1));
        BalanceResponseDto balanceResponseDto = new BalanceResponseDto();
        balanceResponseDto.setBalance(new BigDecimal(100));
        when(cartRepository.findByAccountIdAndStatus(USER_ID, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(exceptingCart));
        when(productRepository.saveAll(anyIterable())).thenReturn(Flux.empty());
        when(accountService.processOrder(any(), any())).thenReturn(Mono.just(balanceResponseDto));
        when(session.getAttribute("X-Ver")).thenReturn("1");


        StepVerifier.create(cartService.confirmCart(processPaymentDto, session))
                .assertNext(cartId -> {
                    assertEquals(exceptingCart.getCartId(), cartId);
                    assertNotNull(exceptingCart.getConfirmedDate());
                    assertEquals(Status.DONE, exceptingCart.getStatus());
                })
                .verifyComplete();

        verify(cartRepository, times(1)).findByAccountIdAndStatus(USER_ID, Status.CREATED);
    }

    @Test
    void getConfirmCartListTest() {
        List<ConfirmCart> exceptedConfirmCarts = getConfirmCarts();
        when(cartRepository.getConfirmCartsByUserIp(USER_ID)).thenReturn(Flux.fromIterable(exceptedConfirmCarts));
        when(cartMapper.mapToConfirmCartDto(anyList())).thenReturn(new ListConfirmCartDto(exceptedConfirmCarts.stream()
                .map(cart -> new ConfirmCart(cart.id(), cart.confirmed_date(), cart.cart_price()))
                .toList(), new BigDecimal("2")));

        StepVerifier.create(cartService.getConfirmCartList(USER_ID))

                .assertNext(response -> {
                    assertEquals(exceptedConfirmCarts.getFirst().id(), response.confirmCarts().getFirst().id());
                    assertEquals(exceptedConfirmCarts.getFirst().cart_price(), response.confirmCarts().getFirst().cart_price());
                    assertNotNull(response.confirmCarts().getFirst().confirmed_date());
                })
                .verifyComplete();

        verify(cartRepository, times(1)).getConfirmCartsByUserIp(USER_ID);
    }

    @Test
    @SneakyThrows
    void addToCartTestWhenProductNotExistsInCart() {
        CreateCartDto exceptingCartDto  = getCreateCartDto();
        Product exceptingProduct = getProduct();
        exceptingProduct.setProductId(2L);
        Cart exceptingCart = getCart();
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Mono.just(exceptingProduct));
        when(cartRepository.findByAccountIdAndStatus(USER_ID, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(cartRepository.save(any())).thenReturn(Mono.just(exceptingCart));
        when(cartItemRepository.saveAll(anyIterable())).thenReturn(Flux.fromIterable(exceptingCart.getOrderedProducts()));

        StepVerifier.create(cartService.addToCart(exceptingCartDto, USER_ID, session))

                .assertNext(response -> assertAll(
                        () -> assertTrue(response.message().contains(exceptingProduct.getProductName())),
                        () -> assertTrue(response.message().contains(exceptingCartDto.count().toString())),
                        () -> verify(productRepository, times(1)).findById(exceptingCartDto.productId()),
                        () -> verify(cartRepository, times(1)).save(exceptingCart)
                ))
                .verifyComplete();
    }

    @Test
    @SneakyThrows
    void addToCartTestWhenCartEmpty() {
        CreateCartDto exceptingCartDto  = getCreateCartDto();
        Product exceptingProduct = getProduct();
        Cart exceptingCart = getCart();
        exceptingCart.setOrderedProducts(new HashSet<>());
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Mono.just(exceptingProduct));
        when(cartRepository.findByAccountIdAndStatus(USER_ID, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(cartRepository.save(any())).thenReturn(Mono.just(exceptingCart));
        when(cartItemRepository.saveAll(anyIterable())).thenReturn(Flux.fromIterable(exceptingCart.getOrderedProducts()));

        StepVerifier.create(cartService.addToCart(exceptingCartDto, USER_ID, session))

                .assertNext(response -> assertAll(
                        () -> assertTrue(response.message().contains(exceptingProduct.getProductName())),
                        () -> assertTrue(response.message().contains(exceptingCartDto.count().toString())),
                        () -> verify(productRepository, times(1)).findById(exceptingCartDto.productId()),
                        () -> verify(cartRepository, times(1)).save(exceptingCart)
                ))
                .verifyComplete();
    }

    @Test
    @SneakyThrows
    void removeFromCartTestWhenDtoCountLessOrderedProducts() {
        Product exceptingProduct = getProduct();
        DeleteCartDto exceptingCartDto  = getDeleteCartDto();
        Cart exceptingCart = getCart();
        exceptingCart.getOrderedProducts().forEach(cartItems -> cartItems.setCount(10));
        when(cartRepository.findByAccountIdAndStatus(USER_ID, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(productRepository.findById(exceptingCartDto.productId())).thenReturn(Mono.just(exceptingProduct));
        when(cartItemRepository.delete(any())).thenReturn(Mono.empty());
        when(cartItemRepository.save(any())).thenReturn(Mono.empty());

        StepVerifier.create(cartService.removeFromCart(exceptingCartDto, USER_ID, session).contextWrite(getContext()))

                .assertNext(response -> assertAll(
                        () -> verify(cartRepository, times(1)).findByAccountIdAndStatus(USER_ID, Status.CREATED)
                ))
                .verifyComplete();
    }

    @Test
    @SneakyThrows
    void deleteProductFromCartTest() {
        Product exceptingProduct = getProduct();
        Cart exceptingCart = getCart();
        when(cartRepository.findByAccountIdAndStatus(USER_ID, Status.CREATED)).thenReturn(Mono.just(exceptingCart));
        when(productRepository.findById(exceptingProduct.getProductId())).thenReturn(Mono.just(exceptingProduct));
        when(cartItemRepository.delete(any())).thenReturn(Mono.empty());

        StepVerifier.create(cartService.deleteProductFromCart(exceptingProduct.getProductId(), USER_ID, session).contextWrite(getContext()))
                .assertNext(response -> assertAll(
                                () -> verify(productRepository, times(1)).findById(exceptingProduct.getProductId()),
                                () -> verify(cartRepository, times(1)).findByAccountIdAndStatus(USER_ID, Status.CREATED)
                        ))
                .verifyComplete();


    }

    @Test
    @SneakyThrows
    void getConfirmCartTest() {
        Cart exceptingCart = getCart();
        Product exceptingProduct = getProduct();
        exceptingCart.setStatus(Status.DONE);
        when(cartRepository.getFullCartById(exceptingCart.getCartId())).thenReturn(Mono.just(exceptingCart));

        StepVerifier.create(cartService.getConfirmCart(exceptingCart.getCartId()))

                .assertNext(response -> assertAll(
                        () -> assertEquals(response.purchasedProducts().getFirst().productName(), exceptingProduct.getProductName()),
                        () -> assertEquals(response.purchasedProducts().getFirst().price(), exceptingProduct.getPrice()),
                        () -> verify(cartRepository, times(1)).getFullCartById(exceptingCart.getCartId())
                ))
                .verifyComplete();
    }
}
