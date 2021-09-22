# Spring Security JWT
<pre>
<b>JSON-Web-Token을 이용한 간단한 로그인, 회원가입 API 구현</b>
</pre>
## JSON-Web-Token
<pre>
<b>JWT</b> : JSON 객체를 사용해서 토큰 자체에 정보들을 저장하고 있는 Web Token

<b>JWT 구성</b>
- JWT는 Header, Payload, Signature 3개의 부분으로 구성되어져 있다.
- <b>Header</b> : Signature를 해싱하기 위한 알고리즘 정보들로 구성
- <b>Payload</b> : 서버와 클라이언트가 주고받는, 시스템에서 실제로 사용될 정보에 대한 내용들로 구성
- <b>Signature</b> : 토큰의 유효성 검증을 위한 문자열(서버에서는 문자열을 통해서 이 토큰이 유효한지 검증한다)

<b>장점</b>
- 중앙의 인증서버, 데이터 스토어에 대한 의존성이 없음, 시스템 수평 확장 유리
- Base64 URL Safe Encoding을 이용하기 때문에 URL, Cookie, Header 어디에서든 사용 가능

<b>단점</b>
- Payload의 정보가 많아지면 네트워크 사용량 증가, 데이터 설계 고려 필요
- 토큰이 클라이언트에 저장, 서버에서 클라이언트의 토큰을 조작할 수 없음
</pre>
## Entity-relationship Diagram
<pre>
<img src="https://github.com/RyuKyeongWoo/spring-security-JWT/blob/master/img/JWT.PNG"/>
- user와 authority는 다대다(N:M) 관계를 가진다.
- user와 authority는 <b>연결 테이블(매핑 테이블)</b>을 사용하여 다대일(N:1)과 일대다(1:N) 관계로 나타내였다.
</pre>