package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverImageUploadResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

/**
 * 네이버 커머스 이미지 업로드 클라이언트 어댑터.
 *
 * <p>외부 URL에서 이미지를 다운로드한 뒤 네이버 커머스 API에 업로드하여 shop-phinf.pstatic.net 도메인의 URL을 반환합니다.
 *
 * <p>API: POST /v1/product-images/upload (multipart/form-data, field: imageFiles)
 *
 * <p>지원 형식: JPEG, PNG, GIF, BMP
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceImageClientAdapter {

    private static final Logger log =
            LoggerFactory.getLogger(NaverCommerceImageClientAdapter.class);

    private static final String UPLOAD_URI = "/v1/product-images/upload";
    private static final String FIELD_NAME = "imageFiles";

    private final RestClient restClient;
    private final NaverCommerceTokenManager tokenManager;

    public NaverCommerceImageClientAdapter(
            RestClient naverCommerceRestClient, NaverCommerceTokenManager tokenManager) {
        this.restClient = naverCommerceRestClient;
        this.tokenManager = tokenManager;
    }

    /**
     * 외부 이미지 URL을 네이버에 업로드하고 pstatic.net URL을 반환합니다.
     *
     * @param imageUrl 원본 이미지 URL
     * @return 네이버 CDN URL (shop-phinf.pstatic.net)
     * @throws NaverImageUploadException 다운로드 또는 업로드 실패 시
     */
    public String uploadFromUrl(String imageUrl) {
        List<String> urls = uploadFromUrls(List.of(imageUrl));
        return urls.get(0);
    }

    /**
     * 여러 외부 이미지 URL을 한 번에 네이버에 업로드합니다.
     *
     * @param imageUrls 원본 이미지 URL 목록
     * @return 네이버 CDN URL 목록 (입력 순서와 동일)
     * @throws NaverImageUploadException 다운로드 또는 업로드 실패 시
     */
    public List<String> uploadFromUrls(List<String> imageUrls) {
        log.info("네이버 이미지 업로드 시작: {}건", imageUrls.size());

        List<ImageData> downloadedImages = downloadImages(imageUrls);
        MultiValueMap<String, Object> body = buildMultipartBody(downloadedImages);

        NaverImageUploadResponse response = executeUpload(body);

        List<String> uploadedUrls =
                response.images().stream()
                        .map(NaverImageUploadResponse.UploadedImage::url)
                        .toList();

        if (uploadedUrls.size() != imageUrls.size()) {
            throw new NaverImageUploadException(
                    "업로드 결과 개수 불일치: expected="
                            + imageUrls.size()
                            + ", actual="
                            + uploadedUrls.size());
        }

        for (int i = 0; i < uploadedUrls.size(); i++) {
            if (uploadedUrls.get(i) == null || uploadedUrls.get(i).isBlank()) {
                throw new NaverImageUploadException("업로드된 URL이 비어있습니다: index=" + i);
            }
        }

        log.info("네이버 이미지 업로드 성공: {}건", uploadedUrls.size());
        return uploadedUrls;
    }

    /**
     * 바이트 배열로 직접 이미지를 업로드합니다.
     *
     * @param imageBytes 이미지 바이트 배열
     * @param filename 파일명 (확장자 포함)
     * @param contentType MIME 타입 (예: image/jpeg)
     * @return 네이버 CDN URL
     */
    public String uploadBytes(byte[] imageBytes, String filename, String contentType) {
        log.info("네이버 이미지 업로드 (바이트): filename={}, size={}bytes", filename, imageBytes.length);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add(FIELD_NAME, buildFilePart(imageBytes, filename, contentType));

        NaverImageUploadResponse response = executeUpload(body);

        String uploadedUrl = response.images().get(0).url();
        log.info("네이버 이미지 업로드 성공: {} → {}", filename, uploadedUrl);
        return uploadedUrl;
    }

    private NaverImageUploadResponse executeUpload(MultiValueMap<String, Object> body) {
        String token = tokenManager.getAccessToken();

        NaverImageUploadResponse response =
                restClient
                        .post()
                        .uri(UPLOAD_URI)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "Bearer " + token)
                        .body(body)
                        .retrieve()
                        .body(NaverImageUploadResponse.class);

        if (response == null || response.images() == null || response.images().isEmpty()) {
            throw new NaverImageUploadException("네이버 이미지 업로드 응답이 비어있습니다");
        }

        return response;
    }

    private MultiValueMap<String, Object> buildMultipartBody(List<ImageData> images) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (ImageData image : images) {
            body.add(FIELD_NAME, buildFilePart(image.bytes, image.filename, image.contentType));
        }
        return body;
    }

    private HttpEntity<ByteArrayResource> buildFilePart(
            byte[] bytes, String filename, String contentType) {
        ByteArrayResource resource = new NamedByteArrayResource(bytes, filename);

        HttpHeaders partHeaders = new HttpHeaders();
        partHeaders.setContentType(MediaType.parseMediaType(contentType));

        return new HttpEntity<>(resource, partHeaders);
    }

    private List<ImageData> downloadImages(List<String> imageUrls) {
        List<ImageData> results = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            results.add(downloadImage(imageUrl));
        }
        return results;
    }

    private ImageData downloadImage(String imageUrl) {
        try {
            HttpURLConnection conn =
                    (HttpURLConnection) URI.create(imageUrl).toURL().openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(30_000);

            try (InputStream in = conn.getInputStream()) {
                byte[] bytes = in.readAllBytes();

                String filename = extractFilename(imageUrl);
                String responseContentType = conn.getContentType();
                String contentType =
                        responseContentType != null && responseContentType.startsWith("image/")
                                ? responseContentType.split(";")[0].trim()
                                : guessContentType(filename);

                log.debug(
                        "이미지 다운로드 완료: url={}, size={}bytes, type={}",
                        maskUrl(imageUrl),
                        bytes.length,
                        contentType);

                return new ImageData(bytes, filename, contentType);
            } finally {
                conn.disconnect();
            }
        } catch (IOException e) {
            throw new NaverImageUploadException(
                    "이미지 다운로드 실패: " + maskUrl(imageUrl) + " - " + e.getMessage(), e);
        }
    }

    private String extractFilename(String imageUrl) {
        String path = URI.create(imageUrl).getPath();
        int lastSlash = path.lastIndexOf('/');
        String filename = lastSlash >= 0 ? path.substring(lastSlash + 1) : path;

        int queryIndex = filename.indexOf('?');
        if (queryIndex > 0) {
            filename = filename.substring(0, queryIndex);
        }

        return filename.isBlank() ? "image.jpg" : filename;
    }

    private String guessContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".png")) {
            return "image/png";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".bmp")) {
            return "image/bmp";
        }
        return "image/jpeg";
    }

    private String maskUrl(String url) {
        int queryIndex = url.indexOf('?');
        return queryIndex > 0 ? url.substring(0, queryIndex) + "?***" : url;
    }

    private record ImageData(byte[] bytes, String filename, String contentType) {}

    /**
     * ByteArrayResource에 파일명을 부여하기 위한 래퍼.
     *
     * <p>Spring의 multipart 변환기가 Content-Disposition에 filename을 포함하려면 getFilename()이 non-null이어야
     * 합니다.
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
            value = "EQ_DOESNT_OVERRIDE_EQUALS",
            justification = "multipart 파일명 래퍼 – equals/hashCode 비교 대상 아님")
    private static class NamedByteArrayResource extends ByteArrayResource {

        private final String filename;

        NamedByteArrayResource(byte[] bytes, String filename) {
            super(bytes);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }
    }

    /** 네이버 이미지 업로드 실패 예외. */
    public static class NaverImageUploadException extends RuntimeException {

        public NaverImageUploadException(String message) {
            super(message);
        }

        public NaverImageUploadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
