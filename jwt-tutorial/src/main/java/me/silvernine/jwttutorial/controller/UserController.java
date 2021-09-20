package me.silvernine.jwttutorial.controller;

import javassist.bytecode.DuplicateMemberException;
import me.silvernine.jwttutorial.dto.UserDto;
import me.silvernine.jwttutorial.entity.User;
import me.silvernine.jwttutorial.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");
    }

    @PostMapping("/test-redirect")
    public void testRedirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/user");
    }

    // 회원가입 API
    // UserDto 를 받아서 UserService 의 signup 메서드를 호출한다.
    @PostMapping("/signup")
    public ResponseEntity<User> signup(
            @Valid @RequestBody UserDto userDto
    ) throws DuplicateMemberException {
        return ResponseEntity.ok(userService.signup(userDto));
    }

    // @PreAuthorize 를 통해서 USER, ADMIN 두가지 권한 모두 호출가능
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<User> getMyUserInfo(HttpServletRequest request) {
        return ResponseEntity.ok(userService.getMyUserWithAuthorities().get());
    }

    // @PreAuthorize 를 통해서 ADMIN 권한만 호출가능
    // UserService 에서 만들었던 getUserWithAuthorities 메서드의 username 파라미터를 기준으로 유저 정보와 권한 정보를 리턴하는 API
    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<User> getUserInfo(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserWithAuthorities(username).get());
    }
}
