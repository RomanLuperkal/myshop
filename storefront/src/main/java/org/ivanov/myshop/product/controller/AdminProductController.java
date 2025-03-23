package org.ivanov.myshop.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.dto.UpdateProductDto;
import org.ivanov.myshop.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;

    @GetMapping("products/create")
    public Mono<Rendering> createProductForm(@ModelAttribute("product") ProductCreateDto productCreateDto) {
        return Mono.just(Rendering.view("create-product").build());
    }

    @PostMapping("products")
    public Mono<Rendering> createProduct(@Valid @ModelAttribute("product") ProductCreateDto productCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Mono.just(Rendering.view("create-product").build());
        }
        return productService.createProduct(productCreateDto)
                .thenReturn(Rendering.redirectTo("/admin/products").build());
    }

    @GetMapping("products")
    public Mono<Rendering> getProducts() {
        Rendering r = Rendering.view("admin-product-list")
                .modelAttribute("products", productService.getProducts()).build();
        return Mono.just(r);
    }
    @GetMapping("products/{productId}/update")
    public Mono<Rendering> updateProduct(@PathVariable Long productId, @ModelAttribute("product") UpdateProductDto updateProductDto) {
        updateProductDto.setProductId(productId);
        Rendering r = Rendering.view("update-product").modelAttribute("product", updateProductDto).build();
        return Mono.just(r);
    }

    @PostMapping(value = "products/update")
    public Mono<Rendering> updateProduct(@Valid @ModelAttribute("product") UpdateProductDto updateProductDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Rendering r = Rendering.view("update-product").build();
            return Mono.just(r);
        }
        return productService.updateProduct(updateProductDto).thenReturn(Rendering.redirectTo("/admin/products").build());
    }

    @DeleteMapping(value = "products/{productId}")
    public Mono<Rendering> deleteProduct(@PathVariable Long productId) {
        return productService.deleteProduct(productId).thenReturn(Rendering.redirectTo("/admin/products").build());
    }
}
