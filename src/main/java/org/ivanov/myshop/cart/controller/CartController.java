package org.ivanov.myshop.cart.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.cart.dto.ActualCartResponseDto;
import org.ivanov.myshop.cart.dto.CartResponseDto;
import org.ivanov.myshop.cart.dto.CreateCartDto;
import org.ivanov.myshop.cart.dto.DeleteCartDto;
import org.ivanov.myshop.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService curtService;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<CartResponseDto> addToCart(@RequestBody CreateCartDto dto, HttpServletRequest request) {
        return ResponseEntity.ok(curtService.addToCurt(dto, request.getRemoteAddr()));
    }
    @DeleteMapping("/deleteItem")
    @ResponseBody
    public ResponseEntity<CartResponseDto> deleteFromCart(@RequestBody DeleteCartDto dto, HttpServletRequest request) {
        return ResponseEntity.ok(curtService.removeFromCart(dto, request.getRemoteAddr()));
    }

    @GetMapping("/actual")
    public String getActualCart(HttpServletRequest request, Model model) {
        ActualCartResponseDto cart = curtService.getCart(request.getRemoteAddr());
        model.addAttribute("cart", cart);
        return "cart";
    }

    @DeleteMapping("/deleteProduct/{productId}")
    @ResponseBody
    public ResponseEntity<Void> deleteProductFromCart(@PathVariable Long productId, HttpServletRequest request) {
        curtService.deleteProductFromCart(productId, request.getRemoteAddr());
        return ResponseEntity.noContent().build();
    }
}
