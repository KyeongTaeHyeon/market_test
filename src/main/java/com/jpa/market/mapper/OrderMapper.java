package com.jpa.market.mapper;

import com.jpa.market.dto.OrderHistDto;
import com.jpa.market.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source="id", target="orderId")
    @Mapping(source="orderDate", target="orderDate", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target="orderItemDtoList", ignore = true)
    OrderHistDto entityToDto(Order order);
}
