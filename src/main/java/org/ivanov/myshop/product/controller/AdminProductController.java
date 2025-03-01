package org.ivanov.myshop.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.dto.UpdateProductDto;
import org.ivanov.myshop.product.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminProductController {
    private final ProductService productService;

    @GetMapping("products/create")
    public String createProductForm(@ModelAttribute("product") ProductCreateDto productCreateDto) {
        return "create-product";
    }

    @PostMapping("products")
    public String createProduct(@Valid @ModelAttribute("product") ProductCreateDto productCreateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "create-product";
        }
        productService.createProduct(productCreateDto);
        return "redirect:/admin/products";
    }

    @GetMapping("products")
    public String getProducts(Model model) {
        model.addAttribute("products", productService.getProducts());
        return "admin-product-list";
    }

    @GetMapping("products/{productId}/update")
    public String updateProduct(Model model, @PathVariable Long productId, @ModelAttribute("product") UpdateProductDto updateProductDto) {
        updateProductDto.setProductId(productId);
        model.addAttribute("product", updateProductDto);
        return "update-product";
    }

    @PostMapping(value = "products/update", params = "_method=patch")
    public String updateProduct(@Valid @ModelAttribute("product") UpdateProductDto updateProductDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "update-product";
        }
        productService.updateProduct(updateProductDto);
        return "redirect:/admin/products";
    }

    @PostMapping(value = "products/{productId}", params = "_method=delete")
    public String deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return "redirect:/admin/products";
    }

}
