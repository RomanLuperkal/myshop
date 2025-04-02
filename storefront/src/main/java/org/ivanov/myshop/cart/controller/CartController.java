package org.ivanov.myshop.cart.controller;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.account.dto.ProcessPaymentDto;
import org.ivanov.myshop.cart.dto.*;
import org.ivanov.myshop.cart.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;


    @PostMapping("/add")
    @ResponseBody
    public Mono<CartResponseDto> addToCart(@RequestBody CreateCartDto dto, ServerWebExchange exchange) {
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        return cartService.addToCart(dto, hostAddress);
    }

    @DeleteMapping("/deleteItem")
    @ResponseBody
    public Mono<CartResponseDto> deleteFromCart(@RequestBody DeleteCartDto dto, ServerWebExchange exchange) {
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        return cartService.removeFromCart(dto, hostAddress);
    }

    @GetMapping("/actual")
    public Mono<Rendering> getActualCart(ServerWebExchange exchange) {
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        Rendering r = Rendering.view("cart").modelAttribute("cart", cartService.getActualCart(hostAddress)).build();
        return Mono.just(r);
    }

    @DeleteMapping("/deleteProduct/{productId}")
    @ResponseBody
            //@ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<CartResponseDto> deleteProductFromCart(@PathVariable Long productId, ServerWebExchange exchange) {
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        return cartService.deleteProductFromCart(productId, hostAddress);
    }

    @GetMapping("{cartId}")
    public Mono<Rendering> getConfirmCart(@PathVariable Long cartId, Model model) {
        Mono<ConfirmCartResponseDto> confirmCart = cartService.getConfirmCart(cartId);
        Rendering  r = Rendering.view("confirm-cart").modelAttribute("confirmCart", confirmCart).build();
        return Mono.just(r);
    }

    @PutMapping("/confirm")
    @ResponseBody
    public Mono<Long> confirmCart(ServerWebExchange exchange, @RequestBody ProcessPaymentDto dto) {
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        dto.setUserIp(hostAddress);
        return cartService.confirmCart(dto);
    }

    @GetMapping("/confirm")
    public Mono<Rendering> getConfirmCarts(ServerWebExchange exchange) {
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        Mono<ListConfirmCartDto> listConfirmCartDto = cartService.getConfirmCartList(hostAddress);
        Rendering r = Rendering.view("cart-list").modelAttribute("listConfirmCart", listConfirmCartDto).build();
        return Mono.just(r);
    }

}
