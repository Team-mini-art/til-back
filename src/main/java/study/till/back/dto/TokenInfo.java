package study.till.back.dto;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@Getter
public class TokenInfo {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
