package org.ivanov.myshop.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.ivanov.myshop.cart.CartTestBase;
import org.ivanov.myshop.cart.dto.CartResponseDto;
import org.ivanov.myshop.cart.dto.CreateCartDto;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.repository.CartRepository;
import org.ivanov.myshop.cart_item.model.CartItems;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CartControllerTest extends CartTestBase {
    private final MockMvc mockMvc;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;
    private Product product;

    @BeforeEach
    public void init() {
        product = getProduct();
        product.setProductId(null);
        productRepository.save(product);
    }

    @AfterEach
    public void cleanRepositories()  {
        cartRepository.deleteAll();
        productRepository.deleteAll();
    }


    @Test
    @SneakyThrows
    void addToCartTest() {

        CreateCartDto createCartDto = getCreateCartDto();
        CartResponseDto exceptedJson  = new CartResponseDto("Товар " + product.getProductName() + " в количестве "
                + createCartDto.count() + " шт. добавлен" + " в корзину");
        mockMvc.perform(post("/carts/add")
                        .content(objectMapper.writeValueAsString(createCartDto))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(exceptedJson)));
    }
}
