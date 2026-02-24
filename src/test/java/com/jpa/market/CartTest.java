package com.jpa.market;

import com.jpa.market.dto.MemberJoinDto;
import com.jpa.market.entity.Cart;
import com.jpa.market.entity.Member;
import com.jpa.market.repository.CartRepository;
import com.jpa.market.repository.MemberRepository;
import com.jpa.market.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class CartTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @PersistenceContext
    EntityManager em;

    public MemberJoinDto createMember() {
        MemberJoinDto dto = new MemberJoinDto();

        dto.setLoginId("testuser");
        dto.setPassword("password123");
        dto.setName("Test User");
        dto.setEmail("test@naver.com");
        dto.setAddress("123 Test St, Test City");

        return dto;
    }

    @Test
    @DisplayName("장바구니 - 회원의 매핑테스트")
    public void findCartAndMemberTest() {
        // 회원정보 생성
        MemberJoinDto dto = createMember();

        // 회원가입 실행
        Long savedMemberId = memberService.joinMember(dto);

        // 저장되 ㄴ회원을 찾아옴(Cart와 연결하기 위해)
        Member member = memberRepository.findById(savedMemberId).orElseThrow(EntityNotFoundException::new);

        // 저장된 회원 엔티티를 이용하여 장바구니 생성
        Cart cart = Cart.createCart(member);
        cartRepository.save(cart);

        // 영속성 컨텍스트 반영
        em.flush();
        em.clear();

        System.out.println("로딩 확인");

        // 장바구니정보 조회
        Cart savedCart = cartRepository.findById(cart.getId()).orElseThrow(EntityNotFoundException::new);

        // 가입한 회원의 아이디와 장바구니에 연결된 회원의 아이디가 같은지 확인
        assertThat(savedMemberId).isEqualTo(savedCart.getMember().getId());
    }
}
