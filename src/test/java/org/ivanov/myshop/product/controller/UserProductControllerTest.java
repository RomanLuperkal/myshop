package org.ivanov.myshop.product.controller;

/*import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.ivanov.myshop.product.ProductTestBase;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserProductControllerTest extends ProductTestBase {
    private final MockMvc mockMvc;
    private final ProductRepository productRepository;
    private Product product;

    @BeforeEach
    void init() {
        product = getProduct();
        product.setProductId(null);
        product = productRepository.save(product);
    }

    @AfterEach
    void cleanRepositories() {
        productRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void getProductsTest() {
        mockMvc.perform(get("/products"))

                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("product-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(xpath("//div[@class='top-buttons']/a[@class='btn btn-primary'][text()='Перейти в корзину']").exists())
                .andExpect(xpath("//div[@class='top-buttons']/a[@class='btn btn-success'][text()='История заказов']").exists())
                .andExpect(xpath("//a[@class='admin-login-button'][text()='Войти как администратор']").exists())
                .andExpect(xpath("//input[@id='search'][@placeholder='Поиск по названию/описанию товара']").exists())
                .andExpect(xpath("//select[@id='page-size']").exists())
                .andExpect(xpath("//select[@id='price-filter']").exists())
                .andExpect(xpath("//select[@id='alphabet-filter']").exists())
                .andExpect(xpath("//button[@class='btn btn-primary'][text()='Применить фильтры']").exists())
                .andExpect(xpath("//div[@id='product-list']/div[@class='col-md-4 mb-4']").exists())
                .andExpect(xpath("//div[@id='product-list']/div[@class='col-md-4 mb-4']//h5[@class='card-title'][text()='" + product.getProductName() + "']").exists())
                .andExpect(xpath("//div[@id='product-list']/div[@class='col-md-4 mb-4']//button[@class='btn btn-sm btn-outline-secondary'][text()='Добавить в корзину']").exists())
                .andExpect(xpath("//div[@id='product-list']/div[@class='col-md-4 mb-4']//button[@class='btn btn-sm btn-outline-danger'][text()='Удалить из корзины']").exists())
                .andExpect(xpath("//nav[@aria-label='Page navigation']").exists());
    }
}*/
