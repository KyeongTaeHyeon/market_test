package com.jpa.market.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="order_item")
@Getter
@ToString(exclude = "order")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class OrderItem extends BaseEntity {
    @Id
    @Column(name = "order_item_id",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    private int orderPrice;

    private int count;

    public static OrderItem createOrderItem(Item item, int count) {
        if(count < 1){
            throw new IllegalArgumentException("주문 수량은 1 이상이어야 합니다.");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.item = item;
        orderItem.orderPrice = item.getPrice();
        orderItem.count = count;

        return orderItem;
    }

    public void cancelOrderItem() {
        this.getItem().addStock(count);

        for(OrderItem orderItem : order.getOrderItems()){
            orderItem.cancelOrderItem();
        }
    }
}
