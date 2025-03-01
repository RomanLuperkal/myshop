package org.ivanov.myshop.product.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.ivanov.myshop.product.ProductTestBase;
import org.ivanov.myshop.product.dto.ProductCreateDto;
import org.ivanov.myshop.product.dto.UpdateProductDto;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@WithMockUser(username = "${admin.login}", roles = {"${admin.role}"})
public class AdminProductControllerTest extends ProductTestBase {
    private final MockMvc mockMvc;
    private final ProductRepository productRepository;

    @AfterEach
    void cleanRepositories() {
        productRepository.deleteAll();
    }


    @Test
    @SneakyThrows
    void createProductFormTest() {
        mockMvc.perform(get("/admin/products/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("create-product"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    @SneakyThrows
    void createProductTest() {
        ProductCreateDto productCreateDto = getProductCreateDto();

        mockMvc.perform(post("/admin/products")
                        .flashAttr("product", productCreateDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));
        Iterable<Product> exceptedProducts = productRepository.findAll();
        exceptedProducts
                .forEach(product -> assertEquals(productCreateDto.productName(), product.getProductName()));

    }

    @Test
    void getProductsTest() throws Exception {
        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-product-list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void updateProductFormTest() throws Exception {
        Product product = getProduct();
        product.setProductId(null);
        Product savedProduct = productRepository.save(product);

        mockMvc.perform(get("/admin/products/" + savedProduct.getProductId() + "/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("update-product"))
                .andExpect(model().attributeExists("product"));
    }

    @Test
    void updateProductTest() throws Exception {
        Product product = getProduct();
        product.setProductId(null);
        Product savedProduct = productRepository.save(product);
        UpdateProductDto updateProductDto = getUpdateProductDto();
        updateProductDto.setProductId(savedProduct.getProductId());

        mockMvc.perform(post("/admin/products/update")
                        .param("_method", "patch")
                        .flashAttr("product", updateProductDto))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));
    }

    @Test
    void deleteProductTest() throws Exception {
        Product product = getProduct();
        product.setProductId(null);
        productRepository.save(product);

        mockMvc.perform(post("/admin/products/" +product.getProductId())
                        .param("_method", "delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));
    }
}
