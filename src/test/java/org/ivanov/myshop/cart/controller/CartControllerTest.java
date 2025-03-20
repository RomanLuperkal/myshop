package org.ivanov.myshop.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.ivanov.myshop.cart.CartTestBase;
import org.ivanov.myshop.cart.WebTestClientConfiguration;
import org.ivanov.myshop.cart.dto.CartResponseDto;
import org.ivanov.myshop.cart.dto.CreateCartDto;
import org.ivanov.myshop.cart.dto.DeleteCartDto;
import org.ivanov.myshop.cart.model.Cart;
import org.ivanov.myshop.cart.repository.CartRepository;
import org.ivanov.myshop.cart_item.repository.CartItemRepository;
import org.ivanov.myshop.product.ProductTestBase;
import org.ivanov.myshop.product.mapper.ProductMapper;
import org.ivanov.myshop.product.model.Product;
import org.ivanov.myshop.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.mock;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {WebTestClientConfiguration.class})
@AutoConfigureWebTestClient
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CartControllerTest extends CartTestBase {
    private final WebTestClient webTestClient;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductMapper productMapper;
    private final ObjectMapper objectMapper;
    private final ApplicationContext context;
    private Product product;

    @BeforeEach
    public void init() {
        product = getProduct();
        product.setProductId(null);
        product.setImage(productMapper.getBytesFromPart(ProductTestBase.createMockPart("test_image.jpg")).block());
        product = productRepository.save(product).block();
    }

    @AfterEach
    public void cleanRepositories() {
        cartRepository.deleteAll().block();
        productRepository.deleteAll().block();
    }



    @Test
    @SneakyThrows
    void addToCartTest() {
        CreateCartDto createCartDto = new CreateCartDto(product.getProductId(), 1);
        CartResponseDto expectedJson = new CartResponseDto("Товар " + product.getProductName() + " в количестве "
                + createCartDto.count() + " шт. добавлен" + " в корзину");

        // Вызываем контроллер напрямую (интеграционный тест)
        webTestClient.post()
                .uri("/carts/add")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createCartDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CartResponseDto.class)
                .isEqualTo(expectedJson);
    }

    @Test
    @SneakyThrows
    void deleteFromCartTest() {
        DeleteCartDto deleteCartDto = new DeleteCartDto(product.getProductId(), 1);
        Cart expectedCart = getCart();
        expectedCart.setCartId(null);

        Cart savedCart = cartRepository.save(expectedCart).block();
        Product savedProduct = productRepository.save(product).block();
        expectedCart.getOrderedProducts().forEach(ci -> {
            ci.setCurtItemId(null);
            ci.setCartId(savedCart.getCartId());
            ci.setProduct(product);
            ci.setProductId(savedProduct.getProductId());
        });

        cartItemRepository.saveAll(expectedCart.getOrderedProducts()).blockFirst();


        CartResponseDto expectedJson = new CartResponseDto("Товар " + product.getProductName() + " в количестве "
                + deleteCartDto.count() + " шт. удален из корзины");

        webTestClient.method(HttpMethod.DELETE)
                .uri("/carts/deleteItem")
                .bodyValue(deleteCartDto) // Указываем тело запроса
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(CartResponseDto.class)
                .isEqualTo(expectedJson);
    }

    /*@Test
    @SneakyThrows
    void getActualCartTest() {
        Cart expectedCart = getCart();
        expectedCart.setCartId(null);
        expectedCart.getOrderedProducts().forEach(ci -> {
            ci.setCurtItemId(null);
            ci.setProduct(product);
        });
        cartRepository.save(expectedCart);

        webTestClient.get()
                .uri("/carts/actual")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody()
                .xpath("//tr[@data-product-id]").hasNodeCount(1)
                .xpath("//tr[@data-product-id][1]/td[1][text()='" + product.getProductName() + "']").exists()
                .xpath("//tr[@data-product-id][1]/td[3][contains(text(), '" + product.getPrice() + "')]").exists()
                .xpath("//tr[@data-product-id][1]/td[4][contains(text(), '"
                        + (product.getPrice().multiply(new BigDecimal(1))) + "')]").exists();
    }*/

    /*@Test
    @SneakyThrows
    void deleteProductFromCartTest() {
        Cart cart = getCart();
        cart.setCartId(null);
        cart.getOrderedProducts().forEach(ci -> {
            ci.setCurtItemId(null);
            ci.setProduct(product);
        });
        Cart savedCart = cartRepository.save(cart);

        webTestClient.delete()
                .uri("/carts/deleteProduct/" + product.getProductId())
                .exchange()
                .expectStatus().isNoContent();

        Optional<Cart> optionalCart = cartRepository.findById(savedCart.getCartId());
        assertThat(optionalCart).isPresent();
        assertThat(optionalCart.get().getOrderedProducts()).isEmpty();
    }*/

    /*@Test
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

        webTestClient.get()
                .uri("/carts/" + savedCart.getCartId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody()
                .xpath("//div[@id='order-items']/div[@class='order-item row align-items-center']").hasNodeCount(1)
                .xpath("//div[@class='order-item row align-items-center']/div[@class='col-md-6']/h5[@class='mb-0'][text()='"
                        + product.getProductName() + "']").exists()
                .xpath("//img[@src]").exists();
    }*/

    /*@Test
    @SneakyThrows
    void confirmCartTest() {
        Cart cart = getCart();
        cart.setCartId(null);
        cart.getOrderedProducts().forEach(ci -> {
            ci.setCurtItemId(null);
            ci.setProduct(product);
        });
        Cart savedCart = cartRepository.save(cart);

        webTestClient.put()
                .uri("/carts/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Long.class)
                .isEqualTo(savedCart.getCartId());

        Optional<Cart> optionalCart = cartRepository.findById(savedCart.getCartId());
        assertThat(optionalCart).isPresent();
        assertThat(optionalCart.get().getStatus()).isEqualTo(Status.DONE);
    }*/

    /*@Test
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

        webTestClient.get()
                .uri("/carts/confirm")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody()
                .xpath("//table").exists();
    }*/
}
