package org.ivanov.myshop.product.controller;

import lombok.RequiredArgsConstructor;
import org.ivanov.myshop.product.dto.ListProductDto;
import org.ivanov.myshop.product.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class UserProductController {
    private final ProductService productService;

    @GetMapping
    public Rendering getProducts(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(required = false) List<String> sort, @RequestParam(required = false) String search) {
        Sort sortObj = parseSortParameters(sort);

        // Создаем Pageable с учетом сортировки
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Mono<ListProductDto> products = productService.getProducts(pageable, search);
        Rendering rendering = Rendering.view("product-list").modelAttribute("products", products).build();
        //model.addAttribute("products", productService.getProducts(pageable, search));
        return rendering;
    }

    private Sort parseSortParameters(List<String> sort) {
        if (sort == null || sort.isEmpty()) {
            return Sort.unsorted(); // Если сортировка не указана, возвращаем пустой Sort
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String sortParam : sort) {
            // Разбиваем на поле и направление
            String[] parts = sortParam.split(",");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid sort parameter. Expected format: field,direction");
            }

            String field = parts[0].trim();
            String directionStr = parts[1].trim();

            // Проверяем, что поле не пустое
            if (field.isEmpty()) {
                throw new IllegalArgumentException("Sort field cannot be empty");
            }

            // Проверяем, что направление сортировки допустимо
            if (!directionStr.equalsIgnoreCase("asc") && !directionStr.equalsIgnoreCase("desc")) {
                throw new IllegalArgumentException("Invalid sort direction: " + directionStr + ". Expected 'asc' or 'desc'");
            }

            // Создаем объект Sort.Order
            Sort.Direction direction = Sort.Direction.fromString(directionStr);
            orders.add(new Sort.Order(direction, field));
        }

        return Sort.by(orders);
    }
}
