package com.jpa.market.service;

import com.jpa.market.dto.CartDetailDto;
import com.jpa.market.dto.OrderDto;
import com.jpa.market.entity.Cart;
import com.jpa.market.entity.CartItem;
import com.jpa.market.entity.Item;
import com.jpa.market.entity.Member;
import com.jpa.market.repository.CartItemRepository;
import com.jpa.market.repository.CartRepository;
import com.jpa.market.repository.ItemRepository;
import com.jpa.market.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(String loginId, long itemId, int count) {
        // 상품조회
        Item item = itemRepository.findById(itemId).orElseThrow(()-> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 회원조회
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 로그인한 회원의 장바구니가 있는지 확인
        Cart cart = cartRepository.findByMemberLoginId(loginId);

        // 장바구니가 없으면 새로 생성
        if(cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), itemId);

        if(cartItem != null){
            // - 있으면 수량만 증가
            cartItem.addCount(count);
            return cartItem.getId();
        }
        else {
            // - 없으면 상품을 새로 추가
            cartItem = CartItem.createCartItem(cart, item, count);
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    public List<CartDetailDto> getCartList(String loginId){
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Cart cart = cartRepository.findByMemberLoginId(loginId);

        if(cart == null){
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailList(cart.getId());

        return cartDetailDtoList;
    }

    public void updateCartItemCount(Long cartItemId, int count, String loginId){
        if(count <= 0){
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다."));

        if(!cartItem.getCart().getMember().getLoginId().equals(loginId)) {
            throw new AccessDeniedException("주문 권한 설정이 없습니다.");
        }

        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId, String loginId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(() -> new EntityNotFoundException("장바구니에 상품이 없습니다."));

        if(!cartItem.getCart().getMember().getLoginId().equals(loginId)) {
            throw new AccessDeniedException("주문 권한 설정이 없습니다.");
        }

        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<Long> cartItemIds, String loginId){
        if(cartItemIds == null || cartItemIds.isEmpty()){
            throw new IllegalArgumentException("장바구니 상품을 선택해주세요.");
        }

        List<OrderDto> orderDtoList = new ArrayList<>();

        for(Long cartItemId : cartItemIds){
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(()-> new EntityNotFoundException("장바구니 상품을 찾을 수 없습니다"));

            if(!cartItem.getCart().getMember().getLoginId().equals(loginId)){
                throw new AccessDeniedException("주문 권한 설정이 없습니다.");
            }

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);

            cartItemRepository.delete(cartItem);
        }

        Long orderId = orderService.orderMultipleItems(orderDtoList, loginId);

        for(Long cartItemId : cartItemIds){
            CartItem cartItem = cartItemRepository.findById(cartItemId)
                    .orElseThrow(()-> new EntityNotFoundException("장바구니에 아이템이 없습니다."));
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }
}
