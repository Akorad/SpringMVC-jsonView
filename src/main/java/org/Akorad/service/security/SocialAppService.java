package org.Akorad.service.security;

import lombok.RequiredArgsConstructor;
import org.Akorad.entity.User;
import org.Akorad.service.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.Akorad.entity.Role.USER;

@Service
@RequiredArgsConstructor
public class SocialAppService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;
    private final AuthAuditService auditService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        System.out.println("GitHub user attributes: " + oAuth2User.getAttributes());

        String email = oAuth2User.getAttribute("email");
        String login = oAuth2User.getAttribute("login");
        User user = userService.getUserByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(login);
            newUser.setEmail(email);
            newUser.setPassword(UUID.randomUUID().toString());
            newUser.setRole(USER);
            auditService.logEvent(newUser.getUsername(), "SOCIAL_REGISTER", "New user registered via social provider");
            return userService.createUser(newUser);
        });

        auditService.logEvent(user.getUsername(), "SOCIAL_LOGIN", "User logged in via social provider");

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                oAuth2User.getAttributes(),
                "login"
        );
    }
}
