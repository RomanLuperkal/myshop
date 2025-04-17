package org.ivanov.myshop.product.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ivanov.myshop.product.dto.ListProductDto;
import org.ivanov.myshop.product.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class UserProductController {
    private final ProductService productService;

    @GetMapping
    public Mono<Rendering> getProducts(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) List<String> sort, @RequestParam(required = false) String search,
                                       Authentication auth) {
        Sort sortObj = parseSortParameters(sort);

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Mono<ListProductDto> products = productService.getProducts(pageable, search);
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        Rendering r = Rendering.view("product-list").modelAttribute("products", products)
                .modelAttribute("isAdmin", isAdmin).build();
        return Mono.just(r);
    }

    @GetMapping("{productId}")
    public Mono<Rendering> getProduct(@PathVariable Long productId) {
        Rendering r = Rendering.view("product")
                .modelAttribute("product", productService.getProduct(productId)).build();
        return Mono.just(r);
    }

    private Sort parseSortParameters(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orders = new ArrayList<>();

        if (sortParams.get(1).contains(",")) {
            for (String sortParam : sortParams) {
                String[] sort = sortParam.split(",");
                orders.add(new Sort.Order(Sort.Direction.fromString(sort[1]), sort[0]));
            }
            return Sort.by(orders);
        } else if (sortParams.contains("price") || sortParams.contains("productName")) {
            orders.add(new Sort.Order(Sort.Direction.fromString(sortParams.get(1)), sortParams.get(0)));
            return Sort.by(orders);
        }

        throw new IllegalArgumentException("Invalid sort params");

    }
}
