package com.jpa.market.entity;

import com.jpa.market.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="orders")
@Getter
@ToString(exclude = "orderItems") // 양박향 매핑에서 서로 호출하다 무한루프 발생할 수 있으므로 연관필드 제외
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Order extends BaseEntity{
    @Id
    @Column(name = "order_id",nullable = false)
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    // 양방향매핑
    // 주인이 아닌 Order에서도 OrderItem을 조회할 수 있도록 설정
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    // == 연관관계 편의 메소드 == //
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem); // 1. 내 리스트에 추가
        orderItem.setOrder(this);       // 2. OrderItem에도 내가 주인임을 알려줌
    }

    public static Order createOrder(Member member, List<OrderItem> orderItems) {
        Order order = new Order();

        if(orderItems != null) {
            for(OrderItem orderItem : order.orderItems) {
                order.addOrderItem(orderItem);
            }
        }

        order.member = member;
        order.orderStatus = OrderStatus.ORDER;
        order.orderDate = LocalDateTime.now();
        return order;
    }

    // 전체 주문 금액 계산
    public int getTotalPrice() {
        int totalPrice = 0;

        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getOrderPrice() * orderItem.getCount();
        }

        return totalPrice;
    }

    public void cancelOrder() {

    }
}
