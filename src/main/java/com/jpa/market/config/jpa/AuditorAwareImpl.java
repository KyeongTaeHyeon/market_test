package com.jpa.market.config.jpa;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // 현재 로그인한 사용자의 인증 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = "";
        // 인증 정보에서 사용자의 이름을 가져와서 UserId변수에 저장(LoginId)
        if(authentication != null) {
            userId = authentication.getName();
        }

        // Optional.of() : 값이 Null 일수도 있음을 표현해주는 객체
        // 사용자가 없을 수도 있음을 표시
        return Optional.of(userId);
    }
}
