package org.ivanov.myshop.cart.controller;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.cart.dto.*;
import org.ivanov.myshop.cart.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProductFromCart(@PathVariable Long productId, ServerWebExchange exchange) {
        String hostAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        return cartService.deleteProductFromCart(productId, hostAddress);
    }

    /*@GetMapping("{cartId}")
    public String getConfirmCart(@PathVariable Long cartId, Model model) {
        ConfirmCartResponseDto confirmCart = cartService.getConfirmCart(cartId);
        model.addAttribute("confirmCart", confirmCart);
        return "confirm-cart";
    }*/

   /* @PutMapping("/confirm")
    @ResponseBody
    public ResponseEntity<Long> confirmCart(HttpServletRequest request) {
        Long curtId = cartService.confirmCart(request.getRemoteAddr());
        return ResponseEntity.ok(curtId);
    }*/

    /*@GetMapping("/confirm")
    public String getConfirmCarts(HttpServletRequest request, Model model) {
        ListConfirmCartDto listConfirmCartDto = cartService.getConfirmCartList(request.getRemoteAddr());
        model.addAttribute("listConfirmCart", listConfirmCartDto);
        return "cart-list";
    }*/

}
