package com.jpa.market.service;

import com.jpa.market.constant.OAuthType;
import com.jpa.market.entity.Member;
import com.jpa.market.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registId = userRequest.getClientRegistration().getRegistrationId();

        String providerId = "";
        String nickName = "";
        String email = "";
        OAuthType oAuthType = null;
        String nameAttributeKey = "";

        if("kakao".equals(registId)){
            providerId = attributes.get("id").toString();

            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            nickName = profile.get("nickname").toString();
            email = kakaoAccount.get("email").toString();
            oAuthType = OAuthType.KAKAO;
            nameAttributeKey = "id";
        } else if("naver".equals(registId)){
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");

            System.out.println(response);

            providerId = response.get("id").toString();
            nickName = response.get("name").toString();
            email = response.get("email").toString();
            oAuthType = OAuthType.NAVER;
            nameAttributeKey = "response";
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 로그인 방식입니다.");
        }

        // 소셜타입_소셜ID를 조합하여 로그인 id를 생성(KAKAO_123, NAVER_345)
        String loginId = oAuthType.name() + "_" + providerId;

        // 람다식 안에서 사용하는 지역변수는
        // final 이거나 값을 한번만 대입하고 절대 변경되지 않는 변수여야 함.
        final String finalNickName = nickName;
        final String finalEmail = email;
        final OAuthType finalOAuthType = oAuthType;

        Member member = memberRepository.findByLoginId(loginId).orElseGet(() -> {
            String encodePwd = passwordEncoder.encode("OAUTH_" + UUID.randomUUID());
            Member newMember = Member.createOAuthMember(loginId, finalNickName, finalEmail, encodePwd, finalOAuthType);

            return memberRepository.save(newMember);
        });

        // 시큐리티가 인증 객체로 사용할 OAuthUser 객체를 반환
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().name())),
                attributes,
                nameAttributeKey
        );
    }
}
