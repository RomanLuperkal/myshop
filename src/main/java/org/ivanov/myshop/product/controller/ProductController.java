package org.ivanov.myshop.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/create")
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

    @GetMapping
    public String getProducts(Pageable pageable, Model model) {
        model.addAttribute("products", productService.getProducts(pageable));
        return "product-list";
    }
}
