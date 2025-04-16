package org.ivanov.myshop.cart.controller;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.dto.ProcessPaymentDto;
import org.ivanov.myshop.cart.dto.*;
import org.ivanov.myshop.cart.service.CartService;
import org.ivanov.myshop.configuration.security.AccountUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;


    @PostMapping("/add")
    @ResponseBody
    public Mono<CartResponseDto> addToCart(@RequestBody CreateCartDto dto, ServerWebExchange exchange,
                                           Authentication authentication) {
        Mono<WebSession> session = exchange.getSession();
        return session.flatMap(s -> cartService.addToCart(dto, getUserId(authentication), s));
    }

    @DeleteMapping("/deleteItem")
    @ResponseBody
    public Mono<CartResponseDto> deleteFromCart(@RequestBody DeleteCartDto dto, ServerWebExchange exchange,
                                                Authentication authentication) {
        Mono<WebSession> session = exchange.getSession();
        return session.flatMap(s -> cartService.removeFromCart(dto, getUserId(authentication), s));
    }

    @GetMapping("/actual")
    public Mono<Rendering> getActualCart(ServerWebExchange exchange, Authentication authentication) {
        Mono<WebSession> session = exchange.getSession();
        Rendering r = Rendering.view("cart").modelAttribute("cart",
                session.flatMap(s -> cartService.getActualCart(getUserId(authentication), s))).build();
        return Mono.just(r);
    }

    @DeleteMapping("/deleteProduct/{productId}")
    @ResponseBody
    public Mono<CartResponseDto> deleteProductFromCart(@PathVariable Long productId, ServerWebExchange exchange,
                                                       Authentication authentication) {
        Mono<WebSession> session = exchange.getSession();
        return session.flatMap(s -> cartService.deleteProductFromCart(productId, getUserId(authentication), s));
    }

    @GetMapping("{cartId}")
    public Mono<Rendering> getConfirmCart(@PathVariable Long cartId) {
        Mono<ConfirmCartResponseDto> confirmCart = cartService.getConfirmCart(cartId);
        Rendering  r = Rendering.view("confirm-cart").modelAttribute("confirmCart", confirmCart).build();
        return Mono.just(r);
    }

    @PutMapping("/confirm")
    @ResponseBody
    //TODO проверить работу после интеграции со Spring Security
    public Mono<Long> confirmCart(ServerWebExchange exchange, @RequestBody ProcessPaymentDto dto,
                                  Authentication authentication) {
        Mono<WebSession> session = exchange.getSession();
        dto.setAccountId(getUserId(authentication));
        return session.flatMap(s -> cartService.confirmCart(dto, s));
    }

    @GetMapping("/confirm")
    //TODO проверить работу после интеграции со Spring Security
    public Mono<Rendering> getConfirmCarts(ServerWebExchange exchange, Authentication authentication) {
        Mono<ListConfirmCartDto> listConfirmCartDto = cartService.getConfirmCartList(getUserId(authentication));
        Rendering r = Rendering.view("cart-list").modelAttribute("listConfirmCart", listConfirmCartDto).build();
        return Mono.just(r);
    }

    private Long getUserId(Authentication authentication) {
        AccountUserDetails userDetails = (AccountUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

}
