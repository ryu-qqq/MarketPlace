package com.ryuqq.marketplace.adapter.out.client.authhub.dto;

/**
 * AuthHub 로그인 결과.
 *
 * @param success 성공 여부
 * @param userId 사용자 ID (성공 시)
 * @param accessToken 액세스 토큰 (성공 시)
 * @param refreshToken 리프레시 토큰 (성공 시)
 * @param expiresIn 만료 시간(초) (성공 시)
 * @param tokenType 토큰 타입 (성공 시)
 * @param errorCode 에러 코드 (실패 시)
 * @param errorMessage 에러 메시지 (실패 시)
 */
public record AuthHubLoginResult(
        boolean success,
        String userId,
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType,
        String errorCode,
        String errorMessage) {

    public static AuthHubLoginResult success(
            String userId,
            String accessToken,
            String refreshToken,
            Long expiresIn,
            String tokenType) {
        return new AuthHubLoginResult(
                true, userId, accessToken, refreshToken, expiresIn, tokenType, null, null);
    }

    public static AuthHubLoginResult failure(String errorCode, String errorMessage) {
        return new AuthHubLoginResult(false, null, null, null, null, null, errorCode, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }
}
