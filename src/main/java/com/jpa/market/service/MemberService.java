package com.jpa.market.service;

import com.jpa.market.dto.MemberJoinDto;
import com.jpa.market.entity.Member;
import com.jpa.market.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 패스워드 암호화 처리를 위해서 주입
    private final PasswordEncoder passwordEncoder;

    // 회원가입 처리
    public Long joinMember(MemberJoinDto dto) {
        checkMember(dto);
        Member member = Member.createMember(dto, passwordEncoder);
        memberRepository.save(member);

        return member.getId();
    }

    public void checkMember(MemberJoinDto dto) {
        if (memberRepository.existsMemberByLoginId(dto.getLoginId())) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }

        if (memberRepository.existsMemberByEmail(dto.getEmail())) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }
}
