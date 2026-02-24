package com.jpa.market;

import com.jpa.market.dto.MemberJoinDto;
import com.jpa.market.entity.Member;
import com.jpa.market.repository.MemberRepository;
import com.jpa.market.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class MemberTest {
    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public void saveMemberTest() {
        MemberJoinDto dto = createMember();
        Long savedId = memberService.joinMember(dto);
        if(savedId != null){
            System.out.println("Member saved successfully.");
            System.out.println("Saved Member ID: " + savedId);

            // Optional : 값이 있을 수도 있고, 없을 수 도잇는 객체를 의미
            //            값이 없으면 예외가 발생하는데, Optional은 예외를 안전하게 처리
            // Optional<Member> memberOption = memberRepository.findById(savedId);
            // Member member = memberRepository.findById(savedId).orElse(null);

            // orElseThrow : 값이 없을 때 예외를 던지도록 처리, 있으면 값을 반환
            Member member = memberRepository.findById(savedId).orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + savedId));

            assertThat(member.getLoginId()).isEqualTo(dto.getLoginId());

        } else {
            System.out.println("Member save failed.");
        }
    }

    @Test
    public void saveMemberTest2() {
        MemberJoinDto dto1 = createMember();
        MemberJoinDto dto2 = createMember();

        memberService.joinMember(dto1);

        try {
            memberService.joinMember(dto2);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 아이디입니다.");
        }
    }

    @Test
    @WithMockUser(username="test1", roles="USER")
    public void auditingTest() {
        MemberJoinDto dto = new MemberJoinDto();
        dto.setName("테스트");
        dto.setLoginId("auditing");
        dto.setPassword("1234");
        dto.setEmail("test@naver.com");

        Member newMember = Member.createMember(dto, passwordEncoder);
        memberRepository.save(newMember); // DB에 저장하여 ID 생성

        em.flush();
        em.clear();

        Member member = memberRepository.findById(newMember.getId()).orElseThrow(IllegalArgumentException::new);

        System.out.println("register time : " + member.getRegTime());
        System.out.println("update time : " + member.getUpdateTime());
        System.out.println("create member : " + member.getCreatedBy());
        System.out.println("update member : " + member.getModifiedBy());
    }
}
