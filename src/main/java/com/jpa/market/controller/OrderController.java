package com.jpa.market.controller;

import com.jpa.market.dto.OrderDto;
import com.jpa.market.dto.OrderHistDto;
import com.jpa.market.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<?> order(@RequestBody @Valid OrderDto orderDto,
                                   Principal principal)
    {
        // 로그인한 아이디 가져옴
        String loginId = principal.getName();

        Long orderId = orderService.order(orderDto, loginId);

        return ResponseEntity.ok(orderId);
    }

    @GetMapping(value={"","/{page}"})
    public ResponseEntity orderHist(@PathVariable("page") Optional<Integer> page,
                                     Principal principal)
    {
        String loginId = principal.getName();

        Pageable pageable = PageRequest.of(page.orElse(0), 4);

        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(loginId, pageable);

        // return new ResponseEntity<>(orderHistDtoList, HttpStatus.OK);
        return ResponseEntity.ok(orderHistDtoList);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity deleteOrder(@PathVariable("orderId") Long orderId, Principal principal) throws AccessDeniedException {
        orderService.cancelOrder(orderId, principal.getName());

        return ResponseEntity.ok(orderId);
    }
}
