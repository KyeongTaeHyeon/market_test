package com.jpa.market.mapper;

import com.jpa.market.dto.ItemFormDto;
import com.jpa.market.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface ItemMapper {
    @Mapping(target="itemImgDtoList", source = "itemImgList")
    ItemFormDto entityToDto(Item item);

    @Mapping(target="id", ignore=true)
    @Mapping(target="itemImgList", source="itemImgDtoList")
    Item dtoToEntity(ItemFormDto itemFormDto);
}
