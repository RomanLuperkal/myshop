package org.ivanov.myshop.cart.mapper;

import org.ivanov.myshop.cart.dto.ActualCartResponseDto;
import org.ivanov.myshop.cart_item.mapper.CartItemMapper;
import org.ivanov.myshop.cart_item.model.CartItems;
import org.ivanov.myshop.product.dto.PurchasedProductDto;
import org.ivanov.myshop.product.mapper.ProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartMapper {

    @Mapping(target = "productName", source = "product.productName")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "image", source = "product.image", qualifiedByName = "bytesToBase64")
    PurchasedProductDto mapToPurchasedProductDto(CartItems cartItems);

    List<PurchasedProductDto> mapToPurchasedProductDtoList(Set<CartItems> cartItemsList);

    default ActualCartResponseDto mapToActualCartResponseDto(Set<CartItems> cartItems) {
        CartItemMapper cartItemMapper = Mappers.getMapper(CartItemMapper.class);
        return cartItemMapper.mapToActualCartResponseDto(cartItems);
    }
}
