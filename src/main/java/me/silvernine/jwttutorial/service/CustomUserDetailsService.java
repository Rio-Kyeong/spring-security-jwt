package me.silvernine.jwttutorial.service;

import me.silvernine.jwttutorial.entity.User;
import me.silvernine.jwttutorial.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    // UserRepository 주입
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // UserDetailsService 의 loadUserByUsername 메서드를 오버라이드해서
    // 로그인시에 DB 에서 findOneWithAuthoritiesByUsername 메서드를 통해서 유저정보와 권한정보를 가져오게 된다.
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username)
                .map(user -> createUser(username, user))
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> 데이터베이스에서 찾을 수 없습니다."));
    }


    private org.springframework.security.core.userdetails.User createUser(String username, User user) {
        // 유저 정보가 활성화 상태라면
        if (!user.isActivated()) {
            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
        }

        // 유저의 권한정보(user.getAuthorities())들과
        // 유저 이름(user.getUsername()), 비밀번호(user.getPassword())를 가지고 userdetails.User 객체를 생성해서 반환
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                grantedAuthorities);
    }
}
