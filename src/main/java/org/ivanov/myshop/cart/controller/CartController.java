package org.ivanov.myshop.cart.controller;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.cart.dto.*;
import org.ivanov.myshop.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    /*@DeleteMapping("/deleteItem")
    @ResponseBody
    public ResponseEntity<CartResponseDto> deleteFromCart(@RequestBody DeleteCartDto dto, HttpServletRequest request) {
        return ResponseEntity.ok(cartService.removeFromCart(dto, request.getRemoteAddr()));
    }*/

    /*@GetMapping("/actual")
    public String getActualCart(HttpServletRequest request, Model model) {
        ActualCartResponseDto cart = cartService.getActualCart(request.getRemoteAddr());
        model.addAttribute("cart", cart);
        return "cart";
    }*/

    /*@DeleteMapping("/deleteProduct/{productId}")
    @ResponseBody
    public ResponseEntity<Void> deleteProductFromCart(@PathVariable Long productId, HttpServletRequest request) {
        cartService.deleteProductFromCart(productId, request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }*/

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
