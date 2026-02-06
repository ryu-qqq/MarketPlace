package com.ryuqq.marketplace.adapter.out.client.authhub.dto;

/**
 * AuthHub 토큰 갱신 결과.
 *
 * @param success 성공 여부
 * @param accessToken 액세스 토큰 (성공 시)
 * @param refreshToken 리프레시 토큰 (성공 시)
 * @param accessTokenExpiresIn 액세스 토큰 만료 시간(초) (성공 시)
 * @param refreshTokenExpiresIn 리프레시 토큰 만료 시간(초) (성공 시)
 * @param errorCode 에러 코드 (실패 시)
 * @param errorMessage 에러 메시지 (실패 시)
 */
public record AuthHubRefreshResult(
        boolean success,
        String accessToken,
        String refreshToken,
        Long accessTokenExpiresIn,
        Long refreshTokenExpiresIn,
        String errorCode,
        String errorMessage) {

    public static AuthHubRefreshResult success(
            String accessToken,
            String refreshToken,
            long accessTokenExpiresIn,
            long refreshTokenExpiresIn) {
        return new AuthHubRefreshResult(
                true,
                accessToken,
                refreshToken,
                accessTokenExpiresIn,
                refreshTokenExpiresIn,
                null,
                null);
    }

    public static AuthHubRefreshResult failure(String errorCode, String errorMessage) {
        return new AuthHubRefreshResult(false, null, null, null, null, errorCode, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }
}
