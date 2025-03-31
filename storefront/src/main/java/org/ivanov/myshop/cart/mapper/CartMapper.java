package org.ivanov.myshop.cart.mapper;

import org.ivanov.myshop.cart.dto.ActualCartResponseDto;
import org.ivanov.myshop.cart.dto.ListConfirmCartDto;
import org.ivanov.myshop.cart.proection.ConfirmCart;
import org.ivanov.myshop.cart_item.mapper.CartItemMapper;
import org.ivanov.myshop.cart_item.model.CartItems;
import org.ivanov.myshop.product.dto.PurchasedProductDto;
import org.ivanov.myshop.product.mapper.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartMapper {

    @Mapping(target = "productName", source = "product.productName")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "image", source = "product.image", qualifiedByName = "bytesToBase64")
    PurchasedProductDto mapToPurchasedProductDto(CartItems cartItems);

    List<PurchasedProductDto> mapToPurchasedProductDtoList(Set<CartItems> cartItemsList);

    default ListConfirmCartDto mapToConfirmCartDto(List<ConfirmCart> carts) {
        return new ListConfirmCartDto(carts, calcTotalCount(carts));
    }

    default ActualCartResponseDto mapToActualCartResponseDto(Set<CartItems> cartItems, BigDecimal balance) {
        CartItemMapper cartItemMapper = Mappers.getMapper(CartItemMapper.class);
        return cartItemMapper.mapToActualCartResponseDto(cartItems, balance);
    }

    private BigDecimal calcTotalCount(List<ConfirmCart> carts) {
        BigDecimal total = carts.stream()
                .map(ConfirmCart::cart_price)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
