package org.ivanov.myshop.product.controller;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public String createProduct() {
        return "create-product";
    }

    @PostMapping()
    public String createProduct(ProductCreateDto productCreateDto) {
        productService.createProduct(productCreateDto);
        return "redirect:/product";
    }
}
