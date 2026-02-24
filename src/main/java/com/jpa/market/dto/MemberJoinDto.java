package com.jpa.market.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class MemberJoinDto {
    @NotBlank(message = "로그인 아이디는 필수값입니다.")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수값입니다.")
    @Length(min = 1, max = 20, message = "비밀번호는 1자 이상 20자 이하로 입력해주세요.")
    private String password;

    @NotBlank(message = "이름은 필수값입니다.")
    private String name;

    @NotBlank(message = "이메일은 필수값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    private String address;
}
