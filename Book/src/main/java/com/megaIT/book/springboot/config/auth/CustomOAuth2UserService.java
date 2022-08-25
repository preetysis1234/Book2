package com.megaIT.book.springboot.config.auth;

import com.megaIT.book.springboot.config.auth.dto.OAuthAttributes;
import com.megaIT.book.springboot.config.auth.dto.SessionUser;
import com.megaIT.book.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.megaIT.book.springboot.domain.user.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)throws OAuth2AuthenticationException{

        OAuth2UserService<OAuth2UserRequest,OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();    //로그인 진행중인 서비스를 구분

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        //기본키 값은 역할  >> 얘를 뺴서 데이터 추출을 할 수 있다.
        OAuthAttributes attributes = OAuthAttributes
                .of(registrationId, userNameAttributeName,oAuth2User.getAttributes());  //OAuth2UserService를 통해 가져온 OAuth2User의 attribute에 담는 클래스

        User user = saveOrUpdate(attributes);

        httpSession.setAttribute("user",new SessionUser(user)); //SessionUser객체 생성시  user객체를 받아서 세팅

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());

    }

    private User saveOrUpdate(OAuthAttributes attributes){
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity ->entity.update(attributes.getName(),attributes.getPicture()))
                .orElse(attributes.toEntity());
        return userRepository.save(user);
    }


}
