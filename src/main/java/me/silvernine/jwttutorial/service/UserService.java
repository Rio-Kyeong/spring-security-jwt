package me.silvernine.jwttutorial.service;

import java.util.Collections;
import java.util.Optional;

import javassist.bytecode.DuplicateMemberException;
import me.silvernine.jwttutorial.dto.UserDto;
import me.silvernine.jwttutorial.entity.Authority;
import me.silvernine.jwttutorial.entity.User;
import me.silvernine.jwttutorial.repository.UserRepository;
import me.silvernine.jwttutorial.util.SecurityUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // UserRepository 와 PasswordEncoder 를 주입받는다.
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // UserDto 의 username 을 기준으로 해서 이미 DB 에 저장되어있으면 DuplicateMemberException 발생
    @Transactional
    public User signup(UserDto userDto) throws DuplicateMemberException {
        if (userRepository.findOneWithAuthoritiesByUsername(userDto.getUsername()).orElse(null) != null) {
            throw new DuplicateMemberException("이미 가입되어 있는 유저입니다.");
        }

        // DB 에 저장되어있지 않으면 권한정보(Authority)를 만든다.
        // User 는 "ROLE_USER" 권한 정보를 가진다.
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        // 권한정보를 넣어서 User 정보를 만들어서
        User user = User.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        // 영속화한다.
        return userRepository.save(user);
    }

    // username 을 기준으로 정보(유저정보와 권한정보)를 가져온다.
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByUsername(username);
    }

    // SecurityContext 에 저장된 username 의 정보(유저정보와 권한정보)만 가져온다.
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
    }
}

