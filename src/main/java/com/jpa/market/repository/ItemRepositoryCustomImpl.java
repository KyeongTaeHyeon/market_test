package com.jpa.market.repository;

import com.jpa.market.constant.ItemSellStatus;
import com.jpa.market.dto.*;
import com.jpa.market.entity.QItem;
import com.jpa.market.entity.QItemImg;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

// custom repository를 구현한 클래스를 사용할때는
// 클래스명이 반드시 Impl로 끝나야함.
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    // QueryDsl에서 JPQL쿼리를 만들기 위한 시적점.
    // 객체를 생성하고 .select().from().where()의 체인형태의 SQL문을 조립
    private final JPAQueryFactory queryFactory;

    // EntityManager를 초기화 시키는 생성자
    // QueryDsl은 JPA위에서만 동작하는데, JPA는 EntityManager가 필요
    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 상품의 판매상태에 따라 조건을 설정
    // ItemSellStatus가 있을 때만 WHERE절을 만들고 없으면 조건 자체를 생성하지 않음.
    // BooleanExpression : SQL문의 WHERE 조건 하나를 객체로 표현한 것.
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if(searchDateType == null || "all".equals(searchDateType)){
            return null;
        } else if("1d".equals(searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if("1w".equals(searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if("1m".equals(searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if("6m".equals(searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if("itemName".equals(searchBy)){
            return QItem.item.itemName.like("%" + searchQuery + "%");
        } else if("createdBy".equals(searchBy)){
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }
        return null;
    }

    // Item목록을 Dto로 조회하는 메소드
    @Override
    public Page<ItemAdminListDto> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        // QueryDsl 시작부분
        List<ItemAdminListDto> content = queryFactory
                // 필요한 자료만 조회하도록 엔티티가 아니라 Dto로 조회
                .select(new QItemAdminListDto(
                        QItem.item.id,
                        QItem.item.itemName,
                        QItem.item.itemSellStatus,
                        QItem.item.createdBy,
                        QItem.item.regTime)
                )
                .from(QItem.item)
                .where(
                        // BooleanExpression으로 반환받으므로
                        // null이면 자동으로조건을 무시하고
                        // 조건이 있으면 AND로 결합하여 where을 생성
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(QItem.item.count())
                .from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = queryFactory
                .select(new QMainItemDto(item.id, item.itemName, item.itemDetail, itemImg.imgUrl, item.price))
                .from(item) // 더 강력한 조건을 가지고 있는 엔티티 기준
                .join(itemImg.item, item) // 연관관계가 있으면 ON조건은 자동으로 생성
                .where(itemImg.repImgYn.eq("Y")) // .Join(연관관계필드, 조인될 엔티티 별칭)
                .where(itemNameLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(item.count())
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNameLike(itemSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression itemNameLike(String searchQuery) {
        if(searchQuery != null && !searchQuery.isEmpty()){
            return QItem.item.itemName.like("%" + searchQuery + "%");
        }
        return null;
    }
}
