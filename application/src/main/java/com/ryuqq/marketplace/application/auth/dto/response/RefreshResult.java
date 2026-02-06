package com.ryuqq.marketplace.application.auth.dto.response;

/**
 * 토큰 갱신 결과.
 *
 * @param success 성공 여부
 * @param accessToken 액세스 토큰 (성공 시)
 * @param refreshToken 리프레시 토큰 (성공 시)
 * @param expiresIn 만료 시간(초) (성공 시)
 * @param tokenType 토큰 타입 (성공 시)
 * @param errorCode 에러 코드 (실패 시)
 * @param errorMessage 에러 메시지 (실패 시)
 * @author ryu-qqq
 * @since 1.0.0
 */
public record RefreshResult(
        boolean success,
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType,
        String errorCode,
        String errorMessage) {

    public static RefreshResult success(
            String accessToken, String refreshToken, Long expiresIn, String tokenType) {
        return new RefreshResult(true, accessToken, refreshToken, expiresIn, tokenType, null, null);
    }

    public static RefreshResult failure(String errorCode, String errorMessage) {
        return new RefreshResult(false, null, null, null, null, errorCode, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }
}
