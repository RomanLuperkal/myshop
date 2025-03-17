package org.ivanov.myshop.cart.service;

import lombok.RequiredArgsConstructor;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public Mono<CartResponseDto> addToCart(CreateCartDto dto, String userIp) {
        Mono<Product> findProduct = getProductById(dto.productId()).flatMap(p -> {
            if (p.getCount() < dto.count()) {
                return Mono.error(new ProductException(HttpStatus.CONFLICT, "Недостаточно товара на складе"));
            }
            return Mono.just(p);
        });
        Mono<Cart> actualCart = cartRepository.findByUserIpAndStatus(userIp, Status.CREATED);
        return Mono.zip(findProduct, actualCart).flatMap(tuple -> {
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

            return cartRepository.save(cart)
                    .thenMany(cartItemRepository.saveAll(cart.getOrderedProducts()))
                    .then(Mono.just(new CartResponseDto("Товар " + product.getProductName() +
                            " в количестве " + dto.count() + " шт. добавлен в корзину")));
        });
    }


    @Override
    @Transactional
    public Mono<CartResponseDto> removeFromCart(DeleteCartDto dto, String userIp) {
        Mono<Product> productMono = getProductById(dto.productId());
        Mono<Cart> cartMono = getActualUsrCart(userIp);
        return Mono.zip(productMono, cartMono)
                .flatMap(tuple -> {
                    Product product = tuple.getT1();
                    Cart cart = tuple.getT2();

                    return Mono.justOrEmpty(cart.getOrderedProducts().stream()
                                    .filter(c -> c.getProduct().equals(product))
                                    .findFirst())
                            .switchIfEmpty(Mono.error(new CartException(HttpStatus.CONFLICT, "Товара " + product.getProductName() + " в корзине нет")))
                            .flatMap(cartItems ->
                                    removeProduct(cart, dto, cartItems) // Вызов реактивного метода
                                            .thenReturn(new CartResponseDto("Товар " + product.getProductName() +
                                                    " в количестве " + dto.count() + " шт. удален из корзины"))
                            );
                });
    }

    @Override
    public Mono<ActualCartResponseDto> getActualCart(String userIp) {
        Mono<Cart> cart = getActualUsrCart(userIp);
        return cart.map(c -> cartMapper.mapToActualCartResponseDto(c.getOrderedProducts()));
    }

    @Override
    public Mono<Void> deleteProductFromCart(Long productId, String userIp) {
        Mono<Product> productMono = getProductById(productId);
        Mono<Cart> cartMono = getActualUsrCart(userIp);
        return Mono.zip(productMono, cartMono).flatMap(tuple -> {
            Product product = tuple.getT1();
            Cart cart = tuple.getT2();
            return Mono.justOrEmpty(cart.getOrderedProducts().stream().filter(c -> c.getProduct().equals(product)).findFirst())
                    .switchIfEmpty(Mono.error(new CartException(HttpStatus.CONFLICT, "Товара " + product.getProductName() + " в корзине нет")))
                    .doOnNext(cartItems -> System.out.println(cartItems.getCurtItemId() + ", " + cartItems.getCartId() + ", " + cartItems.getProductId()))
                    .flatMap(cartItemRepository::delete);
        });
        }


    /*@Override
    public ConfirmCartResponseDto getConfirmCart(Long cartId) {
        Cart cart = cartRepository.getFullCartById(cartId)
                .orElseThrow(() -> new CartException(HttpStatus.INTERNAL_SERVER_ERROR, "Корзины c id="+cartId + " не существует"));
        return new ConfirmCartResponseDto(cartMapper.mapToPurchasedProductDtoList(cart.getOrderedProducts()));
    }*/

    /*@Override
    @Transactional
    public Long confirmCart(String userIp) {
        Cart cart = getActualUsrCart(userIp);
        processCart(cart);
        cart.setConfirmedDate(LocalDateTime.now());
        cart.setStatus(Status.DONE);
        return cart.getCartId();
    }*/

    /*@Override
    public ListConfirmCartDto getConfirmCartList(String userIp) {
        List<ConfirmCart> confirmCarts = cartRepository.getConfirmCartsByUserIp(userIp);
        return cartMapper.mapToConfirmCartDto(confirmCarts);
    }*/

    private Mono<Product> getProductById(Long productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error((new ProductException(HttpStatus.NOT_FOUND, "Продукта с id = " + productId + " не существует"))));
    }

    private Mono<Void> removeProduct(Cart cart, DeleteCartDto dto, CartItems cartItems) {
        if (cartItems.getCount() <= dto.count()) {
            // Удаляем элемент из списка корзины и из базы данных
            cart.getOrderedProducts().remove(cartItems);
            return cartItemRepository.delete(cartItems); // Реактивное удаление
        } else {
            // Обновляем количество товара и сохраняем изменения в базе данных
            cartItems.setCount(cartItems.getCount() - dto.count());
            return cartItemRepository.save(cartItems).then(); // Реактивное сохранение
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

    /*private void processCart(Cart cart) {
        cart.getOrderedProducts().forEach(cartItem -> {
            Long stockCount = cartItem.getProduct().getCount();
            int requestedCount = cartItem.getCount();

            if (stockCount < requestedCount) {
                throw new CartException(HttpStatus.CONFLICT, "Кол-во товара на складе изменилось. Пересоздайте заказ");
            }

            // Обновляем количество товара на складе
            cartItem.getProduct().setCount(stockCount - requestedCount);
        });
    }*/
}
