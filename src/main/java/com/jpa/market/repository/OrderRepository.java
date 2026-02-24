package com.jpa.market.repository;

import com.jpa.market.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 전체 개수는 JPA 자동으로 만들어줌
    @Query("select o " +
            "from Order o " +
            "where o.member.loginId = :loginId " +
            "order by o.orderDate desc")
    Page<Order> findOrder(String lgoinId, Pageable pageable);

    // Page<Order> findByMember_LoginIdOrderByOrderDateDesc(String loginId, Pageable pageable);


}
