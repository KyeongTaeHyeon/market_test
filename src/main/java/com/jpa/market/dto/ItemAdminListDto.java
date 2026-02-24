package com.jpa.market.dto;

import com.jpa.market.constant.ItemSellStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemAdminListDto {
    private Long id;
    private String itemName;
    private ItemSellStatus itemSellStatus;
    private String createBy;
    private LocalDateTime regTime;

    // QueryDsl이 DTO 전용 Q클래스를 생성하도록 하는 어노테이션
    // QueryDsl을 사용할 때 엔티티가 아니라 dto타입으로 select하기 위해서 생성자가 필요
    @QueryProjection
    public ItemAdminListDto(Long id, String itemName, ItemSellStatus itemSellStatus,
                            String createBy, LocalDateTime regTime)
    {
        this.id = id;
        this.itemName = itemName;
        this.itemSellStatus = itemSellStatus;
        this.createBy = createBy;
        this.regTime = regTime;
    }
}
