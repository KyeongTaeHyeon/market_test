package com.jpa.market.controller;

import com.jpa.market.dto.CartDetailDto;
import com.jpa.market.dto.CartItemDto;
import com.jpa.market.dto.CartOrderDto;
import com.jpa.market.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> addCartItem(@RequestBody @Valid CartItemDto cartItemDto,
                                      Principal principal) {
        String loginId = principal.getName();
        Long cartItemId = cartService.addCart(loginId, cartItemDto.getItemId(), cartItemDto.getCount());

        return ResponseEntity.ok(Map.of("cartItemId", cartItemId));
    }

    @GetMapping
    public ResponseEntity<?> getCartList(Principal principal){
        List<CartDetailDto> cartdetailDtoList = cartService.getCartList(principal.getName());

        return ResponseEntity.ok(cartdetailDtoList);
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<?> updateCartItemCount(@PathVariable("cartItemId") Long cartItemId,
                                                 @RequestParam("count") int count,
                                                 Principal principal){
        cartService.updateCartItemCount(cartItemId, count, principal.getName());

        return ResponseEntity.ok(cartItemId);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                             Principal principal){
        cartService.deleteCartItem(cartItemId, principal.getName());

        return ResponseEntity.ok(cartItemId);
    }

    @PostMapping("/order")
    public ResponseEntity<?> orderCartType(@RequestBody CartOrderDto cartOrderDto, Principal principal){
        Long orderId = cartService.orderCartItem(cartOrderDto.getCartItemIds(), principal.getName());

        return ResponseEntity.ok(orderId);
    }
}
