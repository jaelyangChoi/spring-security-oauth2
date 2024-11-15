package org.example.springsecurityoauth2.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.springsecurityoauth2.dto.CustomOAuth2User;
import org.example.springsecurityoauth2.dto.GoogleResponse;
import org.example.springsecurityoauth2.dto.NaverResponse;
import org.example.springsecurityoauth2.dto.OAuth2Response;
import org.example.springsecurityoauth2.entity.UserEntity;
import org.example.springsecurityoauth2.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User.getAttributes() = " + oAuth2User.getAttributes());

        // 구글, 네이버 응답 형태가 다르므로 OAuth2Response 라는 인터페이스를 정의하여 각자 파싱
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else
            return null;

        // DB 저장
        String role = "ROLE_USER";
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        UserEntity foundUser = userRepository.findByUsername(username);
        if (foundUser == null) {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setRole(role);
            userRepository.save(userEntity);
        } else {
            role = foundUser.getRole();
            foundUser.setEmail(oAuth2Response.getEmail());
            userRepository.save(foundUser);
        }

        return new CustomOAuth2User(oAuth2Response, role);
    }
}
