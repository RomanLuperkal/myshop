package org.ivanov.myshop.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.cart.dto.*;
import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.mapper.CartMapper;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.repository.CartRepository;
import org.ivanov.myshop.cart_item.model.CartItems;
import org.ivanov.myshop.handler.exception.CartException;
import org.ivanov.myshop.handler.exception.ProductException;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository curtRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponseDto addToCurt(CreateCartDto dto, String userIp) {
        Product product = getProductById(dto.productId());
        if (product.getCount() < dto.count()) {
            throw new ProductException(HttpStatus.CONFLICT, "Недостаточно товара на складе");
        }

        Cart cart = curtRepository.findByUserIpAndStatus(userIp, Status.CREATED).orElseGet(Cart::new);
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

        productRepository.save(product);
        curtRepository.save(cart);
        return new CartResponseDto("Товар " + product.getProductName() + " в количестве " + dto.count() + " шт. добавлен" +
                " в корзину");
    }


    @Override
    @Transactional
    public CartResponseDto removeFromCart(DeleteCartDto dto, String userIp) {
        Product product = getProductById(dto.productId());
        Cart cart = getActualUsrCart(userIp);

        CartItems cartItems = cart.getOrderedProducts().stream().filter(c -> c.getProduct().equals(product))
                .findFirst().orElseThrow(() ->  new CartException(HttpStatus.CONFLICT, "Товара " + product.getProductName() + " в корзине нет"));
        removeProduct(cart, dto, cartItems);
        curtRepository.save(cart);
        return new CartResponseDto("Товар " + product.getProductName() + " в количестве " + dto.count() + " шт. удален из корзины");
    }

    @Override
    public ActualCartResponseDto getActualCart(String userIp) {
        Cart cart = getActualUsrCart(userIp);
        return cartMapper.mapToActualCartResponseDto(cart.getOrderedProducts());
    }

    @Override
    public void deleteProductFromCart(Long productId, String userIp) {
        Product product = getProductById(productId);
        Cart cart = getActualUsrCart(userIp);
        CartItems cartItems = cart.getOrderedProducts().stream().filter(c -> c.getProduct().equals(product)).findFirst()
                .orElseThrow(() -> new CartException(HttpStatus.CONFLICT, "Товара " + product.getProductName() + " в корзине нет"));
        cart.getOrderedProducts().remove(cartItems);
        curtRepository.save(cart);
    }

    @Override
    public ConfirmCartResponseDto getConfirmCart(Long cartId) {
        Cart cart = curtRepository.findById(cartId)
                .orElseThrow(() -> new CartException(HttpStatus.INTERNAL_SERVER_ERROR, "Корзины c id="+cartId + " не существует"));
        return new ConfirmCartResponseDto(cartMapper.mapToPurchasedProductDtoList(cart.getOrderedProducts()));
    }

    @Override
    @Transactional
    public Long confirmCart(String userIp) {
        Cart cart = getActualUsrCart(userIp);
        cart.setConfirmedDate(LocalDateTime.now());
        cart.setStatus(Status.DONE);
        return cart.getCurtId();
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(HttpStatus.NOT_FOUND, "Продукта с id = " + productId + " не существует"));
    }

    private void removeProduct(Cart cart, DeleteCartDto dto, CartItems cartItems) {
        if (cartItems.getCount() <= dto.count()) {
            cart.getOrderedProducts().remove(cartItems);
        } else {
            cartItems.setCount(cartItems.getCount() - dto.count());
        }
    }

    private void prepareOrder(Cart cart, Product product, CreateCartDto dto) {
        CartItems cartItems = new CartItems();
        cartItems.setCart(cart);
        cartItems.setProduct(product);
        cartItems.setCount(dto.count());
        cart.getOrderedProducts().add(cartItems);
    }

    private void checkTotalCount(Product product, CreateCartDto dto, CartItems cartItems) {
        if (product.getCount() < (dto.count() + cartItems.getCount())) {
            throw new ProductException(HttpStatus.CONFLICT, "Недостаточно товара на складе");
        }
    }

    private Cart getActualUsrCart(String userIp) {
        return curtRepository.findByUserIpAndStatus(userIp, Status.CREATED)
                .orElseThrow(() -> new CartException(HttpStatus.INTERNAL_SERVER_ERROR, "Корзины не существует"));
    }
}
