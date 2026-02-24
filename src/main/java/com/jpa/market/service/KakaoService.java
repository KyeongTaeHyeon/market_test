package com.jpa.market.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpa.market.dto.KakaoTokenDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoService {

    public KakaoTokenDto getKakaoAccessToken(String code){
        // 처리에 필요한 url 주소
        String reqUrl = "https://kauth.kakao.com/oauth/token";
        // 스프링에서 ㅈ제공하는 객체로 브라우저 없이 http 요청을 처리할 수 있음
        RestTemplate rt = new RestTemplate();

        //httpHeaders ( http 요청헤더를 생성 )
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "d17c4f39eee160f7ca011df72f8183b8");
        params.add("client_secret","UG06S4DEvYn46Hd1qJvycXKfzKTGV5s1");
        params.add("redirect_uri", "http://localhost:8000/auth/members/kakao");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, httpHeaders);

        ResponseEntity<String> response = rt.exchange(reqUrl, HttpMethod.POST, kakaoTokenRequest, String.class);

        // json과 java의 변환기
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(response.getBody(), KakaoTokenDto.class);
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    public String getKakaoUserInfo(KakaoTokenDto kakaoTokenDto){
        // 처리에 필요한 url 주소
        String reqUrl = "https://kapi.kakao.com/v2/user/me";
        // 스프링에서 ㅈ제공하는 객체로 브라우저 없이 http 요청을 처리할 수 있음
        RestTemplate rt = new RestTemplate();

        //httpHeaders ( http 요청헤더를 생성 )
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("Authorization", "Authorization: Bearer " + kakaoTokenDto.getAccess_token());
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kokoProfileRequest = new HttpEntity<>(httpHeaders);

        ResponseEntity<String> response = rt.exchange(reqUrl, HttpMethod.POST, kokoProfileRequest, String.class);

        return response.getBody();
    }
}
