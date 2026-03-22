package com.ryuqq.marketplace.integration.legacy;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * 레거시 이미지 업로드 세션(Presigned URL) API E2E 테스트.
 *
 * <p>테스트 대상: POST /api/v1/legacy/image/presigned - Presigned URL 발급
 *
 * <p>인증 필요 엔드포인트. StubFileStorageClient가 stub Presigned URL을 반환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("legacy")
@Tag("session")
@DisplayName("레거시 이미지 업로드 세션 API E2E 테스트")
class LegacySessionE2ETest extends LegacyE2ETestBase {

    private static final String PRESIGNED_URL = "/api/v1/legacy/image/presigned";

    @Nested
    @DisplayName("POST /api/v1/legacy/image/presigned - Presigned URL 발급")
    class GetPresignedUrlTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-LSS-S01] 유효한 요청으로 Presigned URL 발급 성공")
        void getPresignedUrl_ValidRequest_Returns200() {
            // given
            Map<String, Object> request = presignedUrlRequest("product-image.jpg", "PRODUCT", null);

            // when & then
            givenLegacyAuth()
                    .body(request)
                    .when()
                    .post(PRESIGNED_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.sessionId", notNullValue())
                    .body("data.preSignedUrl", notNullValue())
                    .body("data.objectKey", notNullValue())
                    .body("response.status", equalTo(200))
                    .body("response.message", equalTo("success"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-LSS-S02] DESCRIPTION 경로로 Presigned URL 발급 성공")
        void getPresignedUrl_DescriptionPath_Returns200() {
            // given
            Map<String, Object> request =
                    presignedUrlRequest("description.jpg", "DESCRIPTION", 2048000L);

            // when & then
            givenLegacyAuth()
                    .body(request)
                    .when()
                    .post(PRESIGNED_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.sessionId", notNullValue())
                    .body("data.preSignedUrl", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-LSS-F01] fileName 누락 시 400 반환")
        void getPresignedUrl_MissingFileName_Returns400() {
            // given
            Map<String, Object> request = Map.of("imagePath", "PRODUCT");

            // when & then
            givenLegacyAuth()
                    .body(request)
                    .when()
                    .post(PRESIGNED_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-LSS-F02] imagePath 누락 시 400 반환")
        void getPresignedUrl_MissingImagePath_Returns400() {
            // given
            Map<String, Object> request = Map.of("fileName", "product-image.jpg");

            // when & then
            givenLegacyAuth()
                    .body(request)
                    .when()
                    .post(PRESIGNED_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-LSS-F03] 빈 문자열 fileName 전달 시 400 반환")
        void getPresignedUrl_BlankFileName_Returns400() {
            // given
            Map<String, Object> request = presignedUrlRequest("", "PRODUCT", null);

            // when & then
            givenLegacyAuth()
                    .body(request)
                    .when()
                    .post(PRESIGNED_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/legacy/image/presigned - 인증 실패 시나리오")
    class GetPresignedUrlAuthTest {

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-LSS-A01] 토큰 없이 요청 시 401 반환")
        void getPresignedUrl_Unauthenticated_Returns401() {
            // given
            Map<String, Object> request = presignedUrlRequest("product-image.jpg", "PRODUCT", null);

            // when & then
            givenUnauthenticated()
                    .body(request)
                    .when()
                    .post(PRESIGNED_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ===== Helper 메서드 =====

    /**
     * Presigned URL 발급 요청 생성.
     *
     * @param fileName 파일명
     * @param imagePath 이미지 경로 구분 (PRODUCT, DESCRIPTION, QNA, CONTENT, IMAGE_COMPONENT, BANNER)
     * @param fileSize 파일 크기 (bytes, null 허용)
     */
    private Map<String, Object> presignedUrlRequest(
            String fileName, String imagePath, Long fileSize) {
        if (fileSize != null) {
            return Map.of("fileName", fileName, "imagePath", imagePath, "fileSize", fileSize);
        }
        return Map.of("fileName", fileName, "imagePath", imagePath);
    }
}
