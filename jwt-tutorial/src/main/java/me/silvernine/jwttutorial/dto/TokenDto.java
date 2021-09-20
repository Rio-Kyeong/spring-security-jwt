package me.silvernine.jwttutorial.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// Token 정보를 Response 할 떄 사용
public class TokenDto {

    private String token;
}
