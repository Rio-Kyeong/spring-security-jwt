package me.silvernine.jwttutorial.config;

import me.silvernine.jwttutorial.jwt.JwtAccessDeniedHandler;
import me.silvernine.jwttutorial.jwt.JwtAuthenticationEntryPoint;
import me.silvernine.jwttutorial.jwt.JwtSecurityConfig;
import me.silvernine.jwttutorial.jwt.TokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity //기본적인 Web 보안을 활성화
@EnableGlobalMethodSecurity(prePostEnabled = true) // @PreAuthorize 어노테이션을 메서드 단위로 추가하기 위해서 적용
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // jwt Class 의존성 주입
    public SecurityConfig(
            TokenProvider tokenProvider,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    // PasswordEncoder 는 BCryptPasswordEncoder 를 사용한다.
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web // h2-console 하위 모든 요청들과 파비콘 관련 요청은 Spring Security 로직을 수행하지 않도록 설정
                .ignoring()
                .antMatchers(
                        "/h2-console/**"
                        ,"/favicon.ico"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 토큰을 사용하기 때문에 csrf 설정은 disable
                .csrf().disable()

                // Exception 을 핸들링할 때 우리가 만들었던 클래스들을 추가해준다.
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                // h2-console 을 위한 설정 추가
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 세션 설정을 STATELESS 로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 로그인 API(authenticate), 회원가입 API(signup)는 토큰이 없는 상태에서 요청이 들어오기 때문에 모두 permitAll 설정
                .and()
                .authorizeRequests() // HttpServletRequest 를 사용하는 요청들에 대한 접근제한을 설정
                .antMatchers("/api/hello").permitAll() // /api/hello 에 대한 요청은 인증없이 접근을 허용
                .antMatchers("/api/authenticate").permitAll() // /api/authenticate 에 대한 요청은 인증없이 접근을 허용
                .antMatchers("/api/signup").permitAll() // /api/signup 에 대한 요청은 인증없이 접근을 허용
                .anyRequest().authenticated() // 나머지 요청들은 모두 인증되어야 한다.

                // JwtFilter 를 addFilterBefore 로 등록했던 JwtSecurityConfig 클래스도 적용
                .and()
                .apply(new JwtSecurityConfig(tokenProvider));
    }
}
