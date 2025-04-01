package org.ivanov.myshop.cart.service;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.client.AccountServiceClient;
import org.ivanov.myshop.account.dto.BalanceResponseDto;
import org.ivanov.myshop.cart.dto.*;
import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.mapper.CartMapper;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.proection.ConfirmCart;
import org.ivanov.myshop.cart.repository.CartRepository;
import org.ivanov.myshop.cart_item.model.CartItems;
import org.ivanov.myshop.cart_item.repository.CartItemRepository;
import org.ivanov.myshop.handler.exception.CartException;
import org.ivanov.myshop.handler.exception.ProductException;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.ProductRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.ContextView;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final CartItemRepository cartItemRepository;
    private final AccountServiceClient accountServiceClient;

    @Override
    @Transactional
    @CacheEvict(value = "cart", key = "#userIp")
    public Mono<CartResponseDto> addToCart(CreateCartDto dto, String userIp) {
        Mono<Product> findProduct = getProductById(dto.productId()).flatMap(p -> {
            if (p.getCount() < dto.count()) {
                return Mono.error(new ProductException(HttpStatus.CONFLICT, "Недостаточно товара на складе"));
            }
            return Mono.just(p);
        });
        Mono<Cart> actualCart = getActualUsrCart(userIp);
        return Mono.zip(findProduct, actualCart).flatMap(tuple -> prepareAddCart(tuple, dto, userIp));
    }


    @Override
    @Transactional
    @CacheEvict(value = "cart", key = "#userIp")
    public Mono<CartResponseDto> removeFromCart(DeleteCartDto dto, String userIp) {
        Mono<Product> productMono = getProductById(dto.productId());
        Mono<Cart> cartMono = getActualUsrCart(userIp);
        return Mono.zip(productMono, cartMono)
                .flatMap(tuple -> prepareRemoveCart(tuple, dto));

    }

    @Override
    @Cacheable(value = "cart", key = "#userIp", unless = "#result == null || #result.cartItems?.isEmpty() || #result.isOrderButtonEnabled == false")
    public Mono<ActualCartResponseDto> getActualCart(String userIp) {
        Mono<BalanceResponseDto> balanceResponseDto = accountServiceClient.getBalance(userIp);
        Mono<Cart> cart = getActualUsrCart(userIp);
        return Mono.zip(cart, balanceResponseDto)
                .flatMap(this::prepareActualCart);
    }

    @Override
    @CacheEvict(value = "cart", key = "#userIp")
    public Mono<CartResponseDto> deleteProductFromCart(Long productId, String userIp) {
        Mono<Product> productMono = getProductById(productId);
        Mono<Cart> cartMono = getActualUsrCart(userIp);
        return Mono.zip(productMono, cartMono).flatMap(this::prepareDeleteProductFromCart);
    }


    @Override
    public Mono<ConfirmCartResponseDto> getConfirmCart(Long cartId) {
        return cartRepository.getFullCartById(cartId)
                .switchIfEmpty(Mono.error(new CartException(HttpStatus.INTERNAL_SERVER_ERROR, "Корзины c id=" + cartId + " не существует")))
                .flatMap(cart -> Mono.just(new ConfirmCartResponseDto(cartMapper.mapToPurchasedProductDtoList(cart.getOrderedProducts()))));

    }

    @Override
    @Transactional
    @CacheEvict(value = "cart", key = "#userIp")
    public Mono<Long> confirmCart(String userIp) {
        return getActualUsrCart(userIp)
                .flatMap(cart -> {
                    cart.setConfirmedDate(LocalDateTime.now());
                    cart.setStatus(Status.DONE);

                    return processCart(cart)
                            .then(cartRepository.save(cart))
                            .thenReturn(cart.getCartId());
                });
    }

    @Override
    public Mono<ListConfirmCartDto> getConfirmCartList(String userIp) {
        Flux<ConfirmCart> findConfirmCarts = cartRepository.getConfirmCartsByUserIp(userIp);
        return findConfirmCarts.collectList().flatMap(confirmCarts -> Mono.just(cartMapper.mapToConfirmCartDto(confirmCarts)));
    }

    private Mono<Product> getProductById(Long productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error((new ProductException(HttpStatus.NOT_FOUND, "Продукта с id = " + productId + " не существует"))));
    }

    private Mono<Void> removeProduct(Cart cart, DeleteCartDto dto, CartItems cartItems) {
        if (cartItems.getCount() <= dto.count()) {
            cart.getOrderedProducts().remove(cartItems);
            return cartItemRepository.delete(cartItems);
        } else {
            cartItems.setCount(cartItems.getCount() - dto.count());
            return cartItemRepository.save(cartItems).then();
        }
    }

    private void prepareOrder(Cart cart, Product product, CreateCartDto dto) {
        CartItems cartItems = new CartItems();
        cartItems.setCart(cart);
        cartItems.setCartId(cart.getCartId());
        cartItems.setProductId(product.getProductId());
        cartItems.setProduct(product);
        cartItems.setCount(dto.count());
        cart.getOrderedProducts().add(cartItems);
    }

    private void checkTotalCount(Product product, CreateCartDto dto, CartItems cartItems) {
        if (product.getCount() < (dto.count() + cartItems.getCount())) {
            throw new ProductException(HttpStatus.CONFLICT, "Недостаточно товара на складе");
        }
    }

    private Mono<Cart> getActualUsrCart(String userIp) {
        return cartRepository.findByUserIpAndStatus(userIp, Status.CREATED)
                .switchIfEmpty(Mono.just(new Cart()));
    }

    private Mono<Void> processCart(Cart cart) {
        Set<Product> soldProducts = new HashSet<>();
        cart.getOrderedProducts().forEach(cartItem -> {
            Long stockCount = cartItem.getProduct().getCount();
            int requestedCount = cartItem.getCount();

            if (stockCount < requestedCount) {
                throw new CartException(HttpStatus.CONFLICT, "Кол-во товара на складе изменилось. Пересоздайте заказ");
            }

            cartItem.getProduct().setCount(stockCount - requestedCount);
            soldProducts.add(cartItem.getProduct());
        });
        return productRepository.saveAll(soldProducts).then();
    }

    private Mono<ActualCartResponseDto> prepareActualCart(Tuple2<Cart, BalanceResponseDto> tuple) {
        return Mono.deferContextual(context -> {
            BigDecimal balance = tuple.getT2().getBalance();
            if (balance == null) {
                return Mono.just(cartMapper.mapToActualCartResponseDto(
                        tuple.getT1().getOrderedProducts(), null
                ));
            }
            WebSession session = context.get("webSession");
            session.getAttributes().put("balance", balance);

            return Mono.just(cartMapper.mapToActualCartResponseDto(
                    tuple.getT1().getOrderedProducts(), balance
            ));
        });
    }

    private Mono<CartResponseDto> prepareAddCart(Tuple2<Product, Cart> tuple, CreateCartDto dto, String userIp) {
        return Mono.deferContextual(context -> {
            Cart cart = tuple.getT2();
            Product product = tuple.getT1();
            if (cart.getOrderedProducts().isEmpty()) {
                cart.setUserIp(userIp);
                prepareOrder(cart, product, dto);
            } else {
                Optional<CartItems> curtItems = cart.getOrderedProducts().stream().filter(c -> c.getProduct().equals(product)).findFirst();
                curtItems.ifPresentOrElse(
                        existingItem -> {
                            checkTotalCount(product, dto, existingItem);
                            existingItem.setCount(existingItem.getCount() + dto.count());
                        },
                        () -> prepareOrder(cart, product, dto)
                );
            }

            Boolean isOrderButtonEnabled = isOrderButtonEnabled(context, cart);
            return cartRepository.save(cart)
                    .thenMany(Flux.fromIterable(cart.getOrderedProducts())
                            .map(cartItem -> {
                                cartItem.setCartId(cart.getCartId());
                                return cartItem;
                            })
                            .collectList()
                            .flatMapMany(cartItemRepository::saveAll
                            )
                    )
                    .then(Mono.just(new CartResponseDto("Товар " + product.getProductName() +
                            " в количестве " + dto.count() + " шт. добавлен в корзину", isOrderButtonEnabled)));
        });
    }

    private Mono<CartResponseDto> prepareRemoveCart(Tuple2<Product, Cart> tuple, DeleteCartDto dto) {
        return Mono.deferContextual(context -> {
            Product product = tuple.getT1();
            Cart cart = tuple.getT2();


            return Mono.justOrEmpty(cart.getOrderedProducts().stream()
                            .filter(c -> c.getProduct().equals(product))
                            .findFirst())
                    .switchIfEmpty(Mono.error(new CartException(HttpStatus.CONFLICT, "Товара " + product.getProductName() + " в корзине нет")))
                    .flatMap(cartItems ->
                            removeProduct(cart, dto, cartItems)
                                    .then(Mono.fromCallable(() -> isOrderButtonEnabled(context, cart)))
                                    .map(isOrderButtonEnabled ->
                                            new CartResponseDto(
                                                    "Товар " + product.getProductName() + " в количестве " + dto.count() + " шт. удален из корзины",
                                                    isOrderButtonEnabled
                                            )
                                    ));
        });
    }

    private Mono<CartResponseDto> prepareDeleteProductFromCart(Tuple2<Product, Cart> tuple) {
        return Mono.deferContextual(context -> {
            Product product = tuple.getT1();
            Cart cart = tuple.getT2();

            return Mono.justOrEmpty(cart.getOrderedProducts().stream().filter(c -> c.getProduct().equals(product)).findFirst())
                    .switchIfEmpty(Mono.error(new CartException(HttpStatus.CONFLICT, "Товара " + product.getProductName() + " в корзине нет")))
                    .doOnNext(cartItem -> cart.getOrderedProducts().remove(cartItem))
                    .flatMap(cartItemRepository::delete)
                    .then(Mono.fromCallable(() -> isOrderButtonEnabled(context, cart)))
                    .map(isOrderButtonEnabled -> new CartResponseDto("", isOrderButtonEnabled));
        });
    }

    private BigDecimal calcTotalCount(Cart cart) {
        return cart.getOrderedProducts().stream()
                .map(cartItems -> cartItems.getProduct().getPrice().multiply(new BigDecimal(cartItems.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    private Boolean isOrderButtonEnabled(ContextView context, Cart cart) {
        WebSession session = context.get("webSession");
        BigDecimal balance = (BigDecimal) session.getAttributes().get("balance");
        BigDecimal totalCount = calcTotalCount(cart);
        return balance.compareTo(totalCount) >= 0;
    }
}
