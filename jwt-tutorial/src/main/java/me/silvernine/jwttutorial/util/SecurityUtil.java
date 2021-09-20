package me.silvernine.jwttutorial.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    private SecurityUtil() {
    }

    // Security Context 의 Authentication 객체를 이용해 username 을 리턴해주는 간단한 유틸성 메서드
    public static Optional<String> getCurrentUsername() {
        // SecurityContext 에서 Authentication 객체를 꺼내와서
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // SecurityContext 에서 Authentication 객체가 저장되는 시점은 JwtFilter 의 doFilter 메서드에서
        // Request 가 들어올 때 SecurityContext 에 Authentication 객체를 저장해서 사용한다.

        // 만약 authentication 가 null 이 아니면
        if (authentication == null) {
            logger.debug("Security Context에 인증 정보가 없습니다.");
            return Optional.empty();
        }

        // username 을 반환해준다.
        String username = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            username = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            username = (String) authentication.getPrincipal();
        }

        return Optional.ofNullable(username);
    }
}
