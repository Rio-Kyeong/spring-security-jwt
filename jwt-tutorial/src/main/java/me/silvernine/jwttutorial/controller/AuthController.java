package me.silvernine.jwttutorial.controller;

import me.silvernine.jwttutorial.dto.LoginDto;
import me.silvernine.jwttutorial.dto.TokenDto;
import me.silvernine.jwttutorial.jwt.JwtFilter;
import me.silvernine.jwttutorial.jwt.TokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // TokenProvider, AuthenticationManagerBuilder 를 주입받는다.
    public AuthController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    // 로그인 API
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        // LoginDto 를 파라미터로 받고 이를 이용해서 UsernamePasswordAuthenticationToken 을 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // authenticationToken 토큰을 이용해서 authenticate() 메서드가 실행이 될 때
        // Service 의 loadUserByUsername() 메서드가 실행이 되고 이 결과 값을 가지고
        // Authentication 객체를 생성한다.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 생성된 Authentication 객체를 SecurityContext 에 저장하고
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 그 인증 정보를 기반으로 해서 tokenProvider.createToken() 메서드를 통해서 JWT 토큰을 생성한다.
        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        // JWT 토큰을 Response Header 에 넣어주고
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        // TokenDto 를 이용해서 Response Body 에도 넣어서 리턴한다.
        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }
}
