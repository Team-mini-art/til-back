package study.till.back.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import study.till.back.dto.token.TokenInfo;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtTokenProviderTest extends JwtTokenProvider {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private Key key;
    private TokenInfo tokenInfo;
    private String accessToken;
    private String refreshToken;
    private Long expiredSecond = 1L;

    @Autowired
    public JwtTokenProviderTest(@Value("${jwt.secret}") String secretKey) {
        super(secretKey);
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public TokenInfo generateToken(String memberPk, List<String> roles) {
        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now + expiredSecond * 500);
        // Access Token 생성
        Claims claims = Jwts.claims().setSubject(String.valueOf(memberPk));
        claims.put("roles", roles);
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(accessTokenExpiresIn)
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + expiredSecond * 800))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder().setSigningKey(this.key).build().parseClaimsJws(accessToken).getBody();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
        }
        catch (ExpiredJwtException e) {
        }
        catch (UnsupportedJwtException e) {
        }
        catch (IllegalArgumentException e) {
        }
        return false;
    }

    @BeforeEach
    void initToken() {
        //given
        String memberPk = "a@a.com";
        List<String> roles = new ArrayList<>();
        roles.add("testUser");
        this.tokenInfo = this.generateToken(memberPk, roles);
        System.out.println("tokenInfo = " + tokenInfo);

        this.accessToken = tokenInfo.getAccessToken();
        this.refreshToken = tokenInfo.getRefreshToken();
    }

    /**
     * 토큰 생성 확인
     */
    @Test
    void generateToken() {
        assertNotNull(tokenInfo);
        assertEquals(tokenInfo.getGrantType(), "Bearer");
    }

    /**
     * 토큰 검증
     */
    @Test
    void validateTokenTest() {
        boolean boolAccess = jwtTokenProvider.validateToken(tokenInfo.getAccessToken());
        boolean boolRefresh = jwtTokenProvider.validateToken(tokenInfo.getRefreshToken());

        System.out.println("boolAccess = " + boolAccess);
        System.out.println("boolRefresh = " + boolRefresh);

        assertEquals(boolAccess, true);
        assertEquals(boolRefresh, true);
    }

    /**
     * Token 만료 검증
     */
    @Test
    void tokenExpirationTest() {
        this.accessToken = tokenInfo.getAccessToken();
        this.refreshToken = tokenInfo.getRefreshToken();

        try {
            //토큰 만료시간보다 긴 시간을 대기한다.
            Thread.sleep((expiredSecond + 5) * 1_000);
        }
        catch (Exception e) {
            fail("Sleep interrupted: " + e.getMessage());
        }

        //access 토큰이 만료되었는지 확인
        assertThrows(
                ExpiredJwtException.class,
                () -> this.parseClaims(accessToken),
                "Access Token should be expired"
        );

        //refresh 토큰이 만료되었는지 확인
        assertThrows(
                ExpiredJwtException.class,
                () -> this.parseClaims(refreshToken),
                "Refresh Token should be expired"
        );
    }

    /**
     * 임의로 만든 Token 검증
     */
    @Test
    void fakeTokenTest() {
        String fakeToken = "123eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        boolean bool = this.validateToken(fakeToken);
        System.out.println("test = " + bool);

        assertEquals(bool, false);
    }

}