package com.jpa.market;

import com.jpa.market.constant.ItemSellStatus;
import com.jpa.market.dto.ItemFormDto;
import com.jpa.market.entity.Item;
import com.jpa.market.entity.QItem;
import com.jpa.market.repository.ItemRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ItemTest {

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void createItemTest() {
        ItemFormDto dto = new ItemFormDto();

        dto.setItemName("Test Item");
        dto.setPrice(1000);
        dto.setStockNumber(50);
        dto.setItemDetail("This is a test item.");
        dto.setItemSellStatus(ItemSellStatus.SELL);

        Item item = Item.createItem(dto);

        Item savedItem = itemRepository.save(item);

        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getRegTime()).isNotNull();

        System.out.println("Saved Item: " + savedItem);
    }

    public void createItemList() {
        for (int i=0; i<10; i++){
            ItemFormDto dto = new ItemFormDto();

            dto.setItemName("Test Item " + (i+1));
            dto.setPrice(1000 * (i+1));
            dto.setStockNumber(50 + i);
            dto.setItemDetail("This is a test item " + (i + 1) + ".");
            dto.setItemSellStatus(ItemSellStatus.SELL);

            Item item = Item.createItem(dto);

            itemRepository.save(item);
        }
    }

    @Test
    public void findByItemNameTest() {
        this.createItemList();

        String itemName = "Test Item 5";
        List<Item> itemList = itemRepository.findByItemName(itemName);

//        for(Item item : itemList) {
//            System.out.println(item);
//        }
        // 컬렉션.forEach() 활용
        itemList.forEach(item -> System.out.println(item));
        // 메서드 레퍼런스 활용
        itemList.forEach(System.out::println);
    }

    @Test
    public void findByItemNameOrItemDetailTest() {
        this.createItemList();

        String itemName = "Test Item 3";
        String itemDetail = "test item 7";

        List<Item> itemList =
                itemRepository.findByItemNameContainingOrItemDetailContaining(itemName, itemDetail);

        itemList.forEach(System.out::println);
    }

    @Test
    public void findByItemDetailTest() {
        this.createItemList();

        List<Item> itemList = itemRepository.findByItemDetail();

        itemList.forEach(System.out::println);
    }

    @Test
    public void queryDslTest() {
        this.createItemList();

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;

        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%test item%"))
                .orderBy(qItem.price.desc());
    }
}
