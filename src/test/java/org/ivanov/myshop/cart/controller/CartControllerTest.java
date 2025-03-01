package org.ivanov.myshop.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.ivanov.myshop.cart.CartTestBase;
import org.ivanov.myshop.cart.dto.CartResponseDto;
import org.ivanov.myshop.cart.dto.CreateCartDto;
import org.ivanov.myshop.cart.dto.DeleteCartDto;
import org.ivanov.myshop.cart.enums.Status;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.repository.CartRepository;
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

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        product = productRepository.save(product);
    }

    @AfterEach
    public void cleanRepositories() {
        cartRepository.deleteAll();
        productRepository.deleteAll();
    }


    @Test
    @SneakyThrows
    void addToCartTest() {

        CreateCartDto createCartDto = new CreateCartDto(product.getProductId(), 1);
        CartResponseDto exceptedJson = new CartResponseDto("Товар " + product.getProductName() + " в количестве "
                + createCartDto.count() + " шт. добавлен" + " в корзину");
        mockMvc.perform(post("/carts/add")
                        .content(objectMapper.writeValueAsString(createCartDto))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(exceptedJson)));
    }

    @Test
    @SneakyThrows
    void deleteFromCartTest() {
        DeleteCartDto deleteCartDto = new DeleteCartDto(product.getProductId(), 1);
        Cart exceptedCart = getCart();
        exceptedCart.setCartId(null);
        exceptedCart.getOrderedProducts().forEach(ci -> {
            ci.setCurtItemId(null);
            ci.setProduct(product);
        });
        cartRepository.save(exceptedCart);
        CartResponseDto exceptedJson = new CartResponseDto("Товар " + product.getProductName() + " в количестве "
                + deleteCartDto.count() + " шт. удален из корзины");
        mockMvc.perform(delete("/carts/deleteItem")
                        .content(objectMapper.writeValueAsString(deleteCartDto))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(exceptedJson)));
    }

    @Test
    @SneakyThrows
    void getActualCartTTest() {
        Cart exceptedCart = getCart();
        exceptedCart.setCartId(null);
        exceptedCart.getOrderedProducts().forEach(ci -> {
            ci.setCurtItemId(null);
            ci.setProduct(product);
        });
        cartRepository.save(exceptedCart);
        mockMvc.perform(get("/carts/actual"))

                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(xpath("//tr[@data-product-id]").nodeCount(1))
                .andExpect(xpath("//tr[@data-product-id][1]/td[1][text()='" + product.getProductName() + "']").exists())
                .andExpect(xpath("//tr[@data-product-id][1]/td[3][contains(text(), '" + product.getPrice() + "')]").exists())
                .andExpect(xpath("//tr[@data-product-id][1]/td[4][contains(text(), '"
                        + (product.getPrice().multiply(new BigDecimal(1))) + "')]").exists());
    }

    @Test
    @SneakyThrows
    @Transactional
    void deleteProductFromCartTest() {
        Cart cart = getCart();
        cart.setCartId(null);
        cart.getOrderedProducts().forEach(ci -> {
            ci.setCurtItemId(null);
            ci.setProduct(product);
        });
        Cart savedCart = cartRepository.save(cart);
        mockMvc.perform(delete("/carts/deleteProduct/" + product.getProductId()))

                .andExpect(status().isNoContent());
        Optional<Cart> exceptedCart = cartRepository.findById(savedCart.getCartId());
        assertNotNull(exceptedCart.get());
        assertTrue(exceptedCart.get().getOrderedProducts().isEmpty());
    }

    @Test
    @SneakyThrows
    void getConfirmCartTest() {
        Cart cart = getCart();
        cart.setCartId(null);
        cart.getOrderedProducts().forEach(ci -> {
            ci.setCurtItemId(null);
            ci.setProduct(product);
        });
        cart.setStatus(Status.DONE);
        Cart savedCart = cartRepository.save(cart);
        mockMvc.perform(get("/carts/" + savedCart.getCartId()))

                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("confirm-cart"))
                .andExpect(model().attributeExists("confirmCart"))
                .andExpect(xpath("//div[@id='order-items']/div[@class='order-item row align-items-center']").nodeCount(1))
                .andExpect(xpath("//div[@class='order-item row align-items-center']/div[@class='col-md-6']/h5[@class='mb-0'][text()='"  + product.getProductName() + "']").exists())
                .andExpect(xpath("//img[@src]").exists());
    }

    @Test
    @SneakyThrows
    void confirmCartTest() {
        Cart cart = getCart();
        cart.setCartId(null);
        cart.getOrderedProducts().forEach(ci -> {
            ci.setCurtItemId(null);
            ci.setProduct(product);
        });
        Cart savedCart = cartRepository.save(cart);
        Cart confirmCart;

        mockMvc.perform(put("/carts/confirm")
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNumber())
                .andExpect(jsonPath("$").value(savedCart.getCartId()));
        confirmCart = cartRepository.findById(savedCart.getCartId()).orElse(null);
        assertNotNull(confirmCart);
        assertEquals(confirmCart.getStatus(), Status.DONE);
    }

    @Test
    @SneakyThrows
    void getConfirmCartsTest() {
        Cart cart = getCart();
        cart.setCartId(null);
        cart.getOrderedProducts().forEach(ci -> {
            ci.setCurtItemId(null);
            ci.setProduct(product);
        });
        cart.setStatus(Status.DONE);
        cartRepository.save(cart);
        mockMvc.perform(get("/carts/confirm"))

                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cart-list"))
                .andExpect(model().attributeExists("listConfirmCart"))
                .andExpect(xpath("//table").exists())
        ;
    }
}
