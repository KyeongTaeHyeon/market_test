package com.jpa.market.repository;

import com.jpa.market.dto.ItemAdminListDto;
import com.jpa.market.dto.ItemSearchDto;
import com.jpa.market.dto.MainItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// jpa는 인터페이스 + 구현클래스만 자동으로 연결처리
// custom을 만드는이유는 1.복합한 쿼리, 2.dto 결과 조회
public interface ItemRepositoryCustom  {

    // Pageable : 페이징 처리를 위한 인터페이스
    Page<ItemAdminListDto> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
