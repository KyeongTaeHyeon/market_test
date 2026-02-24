package com.jpa.market.entity;

import com.jpa.market.constant.OAuthType;
import com.jpa.market.constant.Role;
import com.jpa.market.dto.MemberJoinDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@ToString
@Table(name = "member")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_type")
    private OAuthType oauthType;


    public static Member createMember(MemberJoinDto memberJoinDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.loginId = memberJoinDto.getLoginId();
        // 비밀번호 암호화
        member.password = passwordEncoder.encode(memberJoinDto.getPassword());
        member.name = memberJoinDto.getName();
        member.email = memberJoinDto.getEmail();
        member.address = memberJoinDto.getAddress();
        member.role = Role.USER;
        return member;
    }

    public static Member createOAuthMember(String loginId, String nickName, String email, String password, OAuthType oAuthType) {
        Member member = new Member();
        member.loginId = loginId;
        member.password = password;
        member.name = nickName;
        member.email = email;
        member.role = Role.USER;
        member.oauthType = oAuthType;
        return member;
    }
}
