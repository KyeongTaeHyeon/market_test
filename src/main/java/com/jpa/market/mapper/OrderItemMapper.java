package com.jpa.market.mapper;

import com.jpa.market.dto.OrderItemDto;
import com.jpa.market.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    // OrderItem -> OrderItemDto
    @Mapping(target="itemName", source="orderItem.item.itemName")
    @Mapping(source="imgUrl", target="imgUrl")
    OrderItemDto entityToDto(OrderItem orderItem, String imgUrl);
}
