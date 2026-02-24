package com.jpa.market.repository;

import com.jpa.market.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // findBy.... : DB에서 조건에 맞는 엔티티를 조회할 때 사용
    //              반환타입에 따라 다르게 동작
    // existsBy... : DB에서 조건에 맞는 엔티티가 존재하는지 여부를 확인할 때 사용
    //              true/false 반환
    //              select count(*) from member where login_id = ? > 0 -> true/false
    // id 중복검사
    Boolean existsMemberByLoginId(String loginId);

    Boolean existsMemberByEmail(String email);

    Optional<Member> findByLoginId(String loginId);
}
