package org.ivanov.myshop.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public String createProductForm(@ModelAttribute("product") ProductCreateDto productCreateDto) {
        return "create-product";
    }

    @PostMapping()
    public String createProduct(@Valid @ModelAttribute("product") ProductCreateDto productCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "create-product";
        }
        productService.createProduct(productCreateDto);
        return "redirect:/product";
    }
}
