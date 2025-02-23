package org.ivanov.myshop.cart.mapper;

import org.ivanov.myshop.cart.dto.ActualCartResponseDto;
import org.ivanov.myshop.cart_item.mapper.CartItemMapper;
import org.ivanov.myshop.cart_item.model.CartItems;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {

    default ActualCartResponseDto mapToActualCartResponseDto(Set<CartItems> cartItems) {
        CartItemMapper cartItemMapper = Mappers.getMapper(CartItemMapper.class);
        return cartItemMapper.mapToActualCartResponseDto(cartItems);
    }
}
