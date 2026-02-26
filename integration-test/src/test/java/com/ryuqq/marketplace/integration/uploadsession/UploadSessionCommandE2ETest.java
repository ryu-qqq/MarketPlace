package com.ryuqq.marketplace.integration.uploadsession;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.ryuqq.marketplace.integration.E2ETestBase;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * UploadSession Command API E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>POST /upload-sessions - Presigned URL 발급
 *   <li>POST /upload-sessions/{sessionId}/complete - 업로드 완료 처리
 * </ul>
 *
 * <p>보안: @PreAuthorize("@access.authenticated()") + @RequirePermission("file:write")
 *
 * <p>스텁: StubExternalClientConfig의 stubFileStorageClient 사용
 *
 * <ul>
 *   <li>generateUploadUrl() → stub-session-id, stub-presigned-url 반환
 *   <li>completeUploadSession() → no-op
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("upload-session")
@Tag("command")
@DisplayName("[E2E] UploadSession Command API 테스트")
class UploadSessionCommandE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/upload-sessions";

    // ===== POST /upload-sessions - Presigned URL 발급 =====

    @Nested
    @DisplayName("POST /upload-sessions - Presigned URL 발급")
    class GenerateUploadUrlTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1-1] 유효한 요청 → 201 Created + 응답 필드 검증")
        void generateUploadUrl_validRequest_returns201WithFields() {
            // given
            Map<String, Object> request = createGenerateUploadUrlRequest();

            // when & then
            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.sessionId", notNullValue())
                    .body("data.presignedUrl", notNullValue())
                    .body("data.fileKey", notNullValue())
                    .body("data.expiresAt", notNullValue())
                    .body("data.accessUrl", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-2] Stub 반환값 검증 - sessionId, presignedUrl 정확도 확인")
        void generateUploadUrl_validRequest_returnsStubValues() {
            // given
            Map<String, Object> request = createGenerateUploadUrlRequest();

            // when & then: StubFileStorageClient 반환값 검증
            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.sessionId", equalTo("stub-session-id"))
                    .body("data.presignedUrl", equalTo("https://stub-presigned-url.example.com"))
                    .body("data.fileKey", equalTo("stub-file-key"))
                    .body("data.accessUrl", equalTo("https://stub-access-url.example.com"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-3] directory 빈 값 → 400 Bad Request")
        void generateUploadUrl_blankDirectory_returns400() {
            // given
            Map<String, Object> request = new HashMap<>();
            request.put("directory", "");
            request.put("filename", "image.jpg");
            request.put("contentType", "image/jpeg");
            request.put("contentLength", 1048576);

            // when & then
            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-4] filename 빈 값 → 400 Bad Request")
        void generateUploadUrl_blankFilename_returns400() {
            // given
            Map<String, Object> request = new HashMap<>();
            request.put("directory", "product-images");
            request.put("filename", "");
            request.put("contentType", "image/jpeg");
            request.put("contentLength", 1048576);

            // when & then
            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-5] contentType 빈 값 → 400 Bad Request")
        void generateUploadUrl_blankContentType_returns400() {
            // given
            Map<String, Object> request = new HashMap<>();
            request.put("directory", "product-images");
            request.put("filename", "image.jpg");
            request.put("contentType", "");
            request.put("contentLength", 1048576);

            // when & then
            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-6] contentLength 0 이하 → 400 Bad Request")
        void generateUploadUrl_nonPositiveContentLength_returns400() {
            // given
            Map<String, Object> request = new HashMap<>();
            request.put("directory", "product-images");
            request.put("filename", "image.jpg");
            request.put("contentType", "image/jpeg");
            request.put("contentLength", 0);

            // when & then
            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-7] 비인증 사용자 → 401 Unauthorized")
        void generateUploadUrl_unauthenticated_returns401() {
            // given
            Map<String, Object> request = createGenerateUploadUrlRequest();

            // when & then
            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-8] 인증된 사용자는 file:write 권한 없어도 201 반환 (authenticated() 체크만 적용)")
        void generateUploadUrl_authenticatedWithoutFileWritePermission_returns201() {
            // given
            // 참고: @PreAuthorize("@access.authenticated()")만 적용되어 있어
            // @RequirePermission("file:write")는 런타임에 강제되지 않음 (문서화 목적)
            Map<String, Object> request = createGenerateUploadUrlRequest();

            // when & then: product-group:write 권한만 보유해도 201 반환
            given().spec(givenSellerUser("org-seller-001", "product-group:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.sessionId", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-9] SuperAdmin도 요청 가능 → 201 Created")
        void generateUploadUrl_superAdmin_returns201() {
            // given
            Map<String, Object> request = createGenerateUploadUrlRequest();

            // when & then
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data.sessionId", notNullValue());
        }
    }

    // ===== POST /upload-sessions/{sessionId}/complete - 업로드 완료 처리 =====

    @Nested
    @DisplayName("POST /upload-sessions/{sessionId}/complete - 업로드 완료 처리")
    class CompleteUploadSessionTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2-1] etag 포함 완료 처리 → 200 OK")
        void completeUploadSession_withEtag_returns200() {
            // given
            String sessionId = "stub-session-id";
            Map<String, Object> request = new HashMap<>();
            request.put("fileSize", 1048576);
            request.put("etag", "\"d41d8cd98f00b204e9800998ecf8427e\"");

            // when & then
            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL + "/{sessionId}/complete", sessionId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", nullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-2] etag null (CORS 제한 시나리오) → 200 OK")
        void completeUploadSession_withNullEtag_returns200() {
            // given: CORS 제한으로 etag를 전달하지 못하는 시나리오
            String sessionId = "stub-session-id";
            Map<String, Object> request = new HashMap<>();
            request.put("fileSize", 1048576);
            // etag 필드 생략 (null)

            // when & then
            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL + "/{sessionId}/complete", sessionId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", nullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-3] fileSize 0 이하 → 400 Bad Request")
        void completeUploadSession_nonPositiveFileSize_returns400() {
            // given
            String sessionId = "stub-session-id";
            Map<String, Object> request = new HashMap<>();
            request.put("fileSize", 0);
            request.put("etag", "\"d41d8cd98f00b204e9800998ecf8427e\"");

            // when & then
            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL + "/{sessionId}/complete", sessionId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-4] 음수 fileSize → 400 Bad Request")
        void completeUploadSession_negativeFileSize_returns400() {
            // given
            String sessionId = "stub-session-id";
            Map<String, Object> request = new HashMap<>();
            request.put("fileSize", -1);
            request.put("etag", "\"abc\"");

            // when & then
            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL + "/{sessionId}/complete", sessionId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-5] 비인증 사용자 → 401 Unauthorized")
        void completeUploadSession_unauthenticated_returns401() {
            // given
            Map<String, Object> request = createCompleteUploadSessionRequest("\"abc\"");

            // when & then
            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post(BASE_URL + "/{sessionId}/complete", "stub-session-id")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C2-6] 인증된 사용자는 file:write 권한 없어도 200 반환 (authenticated() 체크만 적용)")
        void completeUploadSession_authenticatedWithoutFileWritePermission_returns200() {
            // given
            // 참고: @PreAuthorize("@access.authenticated()")만 적용되어 있어
            // @RequirePermission("file:write")는 런타임에 강제되지 않음 (문서화 목적)
            Map<String, Object> request = createCompleteUploadSessionRequest("\"abc\"");

            // when & then: 인증된 사용자면 통과
            given().spec(givenSellerUser("org-seller-001", "product-group:read"))
                    .body(request)
                    .when()
                    .post(BASE_URL + "/{sessionId}/complete", "stub-session-id")
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }

    // ===== 전체 플로우 시나리오 =====

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] Presigned URL 발급 → 업로드 완료 처리 (etag 포함) 전체 흐름")
        void fullFlow_generateThenComplete_withEtag() {
            // Step 1: Presigned URL 발급
            Map<String, Object> generateRequest = createGenerateUploadUrlRequest();

            String sessionId =
                    given().spec(givenSellerUser("org-seller-001", "file:write"))
                            .body(generateRequest)
                            .when()
                            .post(BASE_URL)
                            .then()
                            .statusCode(HttpStatus.CREATED.value())
                            .body("data.sessionId", notNullValue())
                            .body("data.presignedUrl", notNullValue())
                            .extract()
                            .path("data.sessionId");

            // Step 2: 업로드 완료 처리 (S3 업로드 후 클라이언트가 호출)
            Map<String, Object> completeRequest =
                    createCompleteUploadSessionRequest("\"d41d8cd98f00b204e9800998ecf8427e\"");

            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(completeRequest)
                    .when()
                    .post(BASE_URL + "/{sessionId}/complete", sessionId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", nullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[F2] Presigned URL 발급 → 업로드 완료 처리 (CORS 제한, etag null) 전체 흐름")
        void fullFlow_generateThenComplete_withoutEtag() {
            // Step 1: Presigned URL 발급
            Map<String, Object> generateRequest = createGenerateUploadUrlRequest();

            String sessionId =
                    given().spec(givenSellerUser("org-seller-001", "file:write"))
                            .body(generateRequest)
                            .when()
                            .post(BASE_URL)
                            .then()
                            .statusCode(HttpStatus.CREATED.value())
                            .extract()
                            .path("data.sessionId");

            // Step 2: CORS 제한 환경에서 etag 없이 완료 처리
            Map<String, Object> completeRequest = new HashMap<>();
            completeRequest.put("fileSize", 2097152L);
            // etag 미포함

            given().spec(givenSellerUser("org-seller-001", "file:write"))
                    .body(completeRequest)
                    .when()
                    .post(BASE_URL + "/{sessionId}/complete", sessionId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", nullValue());
        }
    }

    // ===== Helper Methods =====

    private Map<String, Object> createGenerateUploadUrlRequest() {
        Map<String, Object> request = new HashMap<>();
        request.put("directory", "product-images");
        request.put("filename", "image.jpg");
        request.put("contentType", "image/jpeg");
        request.put("contentLength", 1048576);
        return request;
    }

    private Map<String, Object> createCompleteUploadSessionRequest(String etag) {
        Map<String, Object> request = new HashMap<>();
        request.put("fileSize", 1048576);
        request.put("etag", etag);
        return request;
    }
}
