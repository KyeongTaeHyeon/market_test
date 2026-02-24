package com.jpa.market.mapper;

import com.jpa.market.dto.ItemImgDto;
import com.jpa.market.entity.ItemImg;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// 인터페이스의 구현체를 만들도록 지정하고 해당 클래스를 스프링Bean으로 등록하도록 설정
@Mapper(componentModel = "spring", uses={ItemImgMapper.class})
public interface ItemImgMapper {

    // 엔티티 -> Dto 변환
    // 결과물타입 메서드의 이름(원본데이터)
    ItemImgDto entityToDto(ItemImg itemImg);

    // 엔티티 : ImgUrl, dto : url
    @Mapping(target="id", ignore=true)
    @Mapping(target="item", ignore=true)
    ItemImg dtoToEntity(ItemImgDto itemImgDto);
}
