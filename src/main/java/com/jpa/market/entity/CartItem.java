package com.jpa.market.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name="cart_item")
@Getter
@ToString
public class CartItem extends BaseEntity {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name="item_id")
    private Item item;

    private int count;

    public static CartItem createCartItem(Cart cart, Item item, int count) {
        if(count < 1){
            throw new IllegalArgumentException("장바구니 수량은 1 이상이어야 합니다.");
        }

        CartItem cartItem = new CartItem();
        cartItem.cart = cart;
        cartItem.item = item;
        cartItem.count = count;
        return cartItem;
    }

    // 이미 담긴 상품 담기
    public void addCount(int count) {
        this.count += count;
    }

    public void updateCount(int count) {
        this.count = count;
    }
}
