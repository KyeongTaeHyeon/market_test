package com.jpa.market.service;

import com.jpa.market.dto.OrderDto;
import com.jpa.market.dto.OrderHistDto;
import com.jpa.market.dto.OrderItemDto;
import com.jpa.market.entity.*;
import com.jpa.market.mapper.OrderItemMapper;
import com.jpa.market.mapper.OrderMapper;
import com.jpa.market.repository.ItemImgRepository;
import com.jpa.market.repository.ItemRepository;
import com.jpa.market.repository.MemberRepository;
import com.jpa.market.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public Long order(OrderDto orderDto, String LoginId){
        Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(() -> new EntityNotFoundException("제품을 찾을 수 없습니다."));

        Member member = memberRepository.findByLoginId(LoginId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 주문처리
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);

        // .of : 불변이라서 생성이후 리스트 수정이 불가능함.
        // List<OrderItem> orderItemList = List.of(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    public Page<OrderHistDto> getOrderList(String loginId, Pageable pageable){
        Page<Order> orderPage = orderRepository.findOrder(loginId, pageable);

        List<OrderHistDto> orderHistDtoList = new ArrayList<>();

        for(Order order: orderPage.getContent()){
            OrderHistDto orderHistDto = orderMapper.entityToDto(order);

            for(OrderItem orderItem : order.getOrderItems()){
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");

                String imgUrl = (itemImg != null) ? itemImg.getImgUrl() : "";
                OrderItemDto orderItemDto = orderItemMapper.entityToDto(orderItem, imgUrl);

                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtoList.add(orderHistDto);
        }

        return new PageImpl<>(orderHistDtoList, pageable, orderPage.getTotalElements());
    }

    public void cancelOrder(Long orderId, String loginId) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));

        if(!order.getMember().getLoginId().equals(loginId)){
            throw new AccessDeniedException("주문을 취소할 권한이 없습니다.");
        }

        order.cancelOrder();
    }

    public Long orderMultipleItems(List<OrderDto> orderDtoList, String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(() -> new EntityNotFoundException("제품을 찾을 수 없습니다."));

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }
}
