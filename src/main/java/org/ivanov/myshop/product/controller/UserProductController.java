package org.ivanov.myshop.product.controller;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.product.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class UserProductController {
    private final ProductService productService;

    @GetMapping
    public String getProducts(@PageableDefault Pageable pageable, Model model, @RequestParam(required = false) String search) {
        model.addAttribute("products", productService.getProducts(pageable, search));
        return "product-list";
    }
}
