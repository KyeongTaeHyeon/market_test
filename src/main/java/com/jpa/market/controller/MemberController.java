package com.jpa.market.controller;

import com.jpa.market.dto.LoginRequestDto;
import com.jpa.market.dto.MemberJoinDto;
import com.jpa.market.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    private final AuthenticationManager authenticationManager;

    // 회원가입
    // @RequestBody : Body에 담겨 있는 Http 요청 정보를 java객체로 변환
    @PostMapping("/join")
    public ResponseEntity<Long> join(@RequestBody @Valid MemberJoinDto dto) {
        Long memberId = memberService.joinMember(dto);

        return ResponseEntity.ok(memberId);
    }

    // ? : 와일드카드, 결과가 텍스트 또는 객체일 수도 있을 때 사용
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request,
                                   HttpServletRequest httpRequest) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword());

            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            HttpSession session = httpRequest.getSession(true);

            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            return ResponseEntity.ok().body(Map.of(
                    "message", "로그인 성공",
                    "loginId", authentication.getName(),
                    "role", authentication.getAuthorities()
                            .stream()
                            .map(a -> a.getAuthority())
                            .toList()
            ));
        }
        catch (Exception e){
            return ResponseEntity.status(401).body(Map.of("message", "아이디 또는 비밀번호가 틀렸습니다."));
        }

    }
}
