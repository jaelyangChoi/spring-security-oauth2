package org.example.springsecurityoauth2.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {
    private final OAuth2Response oAuth2Response;
    private final String role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add((GrantedAuthority) () -> role);
        return authorities;
    }

    @Override
    public String getName() {
        return oAuth2Response.getName();
    }

    public String getUsername() { //id 값으로 사용할 것 정의
        return oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
    }

    // 리소스 서버로부터 넘어오는 모든 데이터
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }
}
