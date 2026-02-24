package com.jpa.market;

import com.jpa.market.constant.ItemSellStatus;
import com.jpa.market.dto.ItemFormDto;
import com.jpa.market.dto.MemberJoinDto;
import com.jpa.market.entity.Item;
import com.jpa.market.entity.Member;
import com.jpa.market.entity.Order;
import com.jpa.market.entity.OrderItem;
import com.jpa.market.repository.ItemRepository;
import com.jpa.market.repository.MemberRepository;
import com.jpa.market.repository.OrderItemRepository;
import com.jpa.market.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Null;
import org.antlr.v4.runtime.misc.LogManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
// @Rollback(value=false)
public class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    OrderItemRepository orderItemRepository;

    @PersistenceContext
    EntityManager em;

    public Item createItem() {
        ItemFormDto dto = new ItemFormDto();

        dto.setItemName("Test Item ");
        dto.setPrice(1000 );
        dto.setStockNumber(50);
        dto.setItemDetail("This is a test item.");
        dto.setItemSellStatus(ItemSellStatus.SELL);

        return Item.createItem(dto);
    }

    public Member createMember() {
        MemberJoinDto dto = new MemberJoinDto();
        dto.setLoginId("tester");
        dto.setPassword("password123");
        dto.setName("Test User");
        dto.setEmail("test@naver.com");
        dto.setAddress("123 Test St, Test City");

        Member member = Member.createMember(dto, passwordEncoder);
        return memberRepository.saveAndFlush(member);
    }

    public Order createOrder() {
        Member member = this.createMember();

        Order order = Order.createOrder(member, null);

        for(int i=0;i<3;i++){
            Item item = this.createItem();
            itemRepository.save(item);

            //OrderItem 생성
            OrderItem orderItem = OrderItem.createOrderItem(item, 10);
            // 부모-자식 연결
            order.getOrderItems().add(orderItem);
            // 자식-부모 연결 (이 부분이 추가되어야 합니다!)
            orderItem.setOrder(order);
        }

        return orderRepository.saveAndFlush(order);
    }

    @Test
    public void orphanRemovalTest() {
        Order order = this.createOrder();

        order.getOrderItems().remove(0);
        em.flush();
    }

    @Test
    public void cascadeTest(){
        Order order = Order.createOrder(null, null);

        for(int i=0;i<3;i++){
            Item item = this.createItem();
            itemRepository.save(item);

            //OrderItem 생성
            OrderItem orderItem = OrderItem.createOrderItem(item, 10);
            // 부모-자식 연결
            order.getOrderItems().add(orderItem);
            // 자식-부모 연결 (이 부분이 추가되어야 합니다!)
            orderItem.setOrder(order);
        }

        // 부ㅁ만 저장 -> 매핑되어 있는 관계까지 자동으로 저장되는지 확인
        orderRepository.saveAndFlush(order);
        // em.flush();
        // orderRepository.save() + em.flush() -> orderRepository.saveAndFlush();

        em.clear();

        Order saveOrder = orderRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);
        assertThat(3).isEqualTo(saveOrder.getOrderItems().size());
    }

    @Test
    public void lazyLodingTest() {
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(EntityNotFoundException::new);

        System.out.println("orderClass : " + orderItem.getOrder().getClass());

        System.out.println("--------------------------------------------------");
        orderItem.getOrder().getOrderDate();
        System.out.println("orderClass : " + orderItem.getOrder().getClass());
        System.out.println("orderClass : " + orderItem.getOrder().getOrderDate());
    }
}
