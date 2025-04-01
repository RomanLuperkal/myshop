package org.ivanov.myshop.cart_item.mapper;

import org.ivanov.myshop.cart.dto.ActualCartResponseDto;
import org.ivanov.myshop.cart_item.dto.CartItemResponseDto;
import org.ivanov.myshop.cart_item.model.CartItems;
import org.ivanov.myshop.product.mapper.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartItemMapper {
    @Mapping(target = "productName", source = "product.productName")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "productId", source = "product.productId")
    CartItemResponseDto mapToCartItemResponseDto(CartItems cartItems);

    List<CartItemResponseDto> mapToCartItemResponseDtoList(Set<CartItems> cartItems);

    default ActualCartResponseDto mapToActualCartResponseDto(Set<CartItems> cartItems, BigDecimal balance) {
        if (balance == null) {
            return new ActualCartResponseDto(mapToCartItemResponseDtoList(cartItems), getTotalCount(cartItems),
                    false, false, "Сервис оплаты временно недоступен");
        }
        if (cartItems.isEmpty()) {
            return new ActualCartResponseDto(mapToCartItemResponseDtoList(cartItems), getTotalCount(cartItems),
                    false, true, "");
        } else {
            BigDecimal totalCount = getTotalCount(cartItems);
            Boolean isOrderButtonEnabled = balance.compareTo(totalCount) >= 0;
            String message = isOrderButtonEnabled ? "" : "На балансе недостаточно средств";
            return new ActualCartResponseDto(mapToCartItemResponseDtoList(cartItems), getTotalCount(cartItems),
                    isOrderButtonEnabled, true,  message);
        }
    }

    default BigDecimal getTotalCount(Set<CartItems> cartItems) {
        BigDecimal total = cartItems.stream()
                .map(c -> c.getProduct().getPrice().multiply(new BigDecimal(c.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
