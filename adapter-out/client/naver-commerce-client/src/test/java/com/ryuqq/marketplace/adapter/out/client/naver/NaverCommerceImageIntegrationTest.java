package com.ryuqq.marketplace.adapter.out.client.naver;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.adapter.NaverCommerceImageClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 네이버 커머스 이미지 업로드 API 실제 호출 통합 테스트.
 *
 * <p>이미지를 네이버에 업로드하여 pstatic.net URL을 받아오는 흐름을 검증합니다.
 *
 * <p>실행 시 환경변수 필요: NAVER_COMMERCE_CLIENT_ID, NAVER_COMMERCE_CLIENT_SECRET
 */
@SpringBootTest(classes = NaverCommerceTestApplication.class)
@ActiveProfiles("naver-test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NaverCommerceImageIntegrationTest {

    /** 테스트용 공개 이미지 URL. */
    private static final String TEST_IMAGE_URL =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/8/80/Wikipedia-logo-v2.svg/200px-Wikipedia-logo-v2.svg.png";

    @Autowired
    private NaverCommerceImageClientAdapter imageClientAdapter;

    @Autowired
    private NaverCommerceTokenManager tokenManager;

    @Test
    @Order(1)
    @DisplayName("토큰 발급 확인")
    void tokenIssuance() {
        String token = tokenManager.getAccessToken();
        assertThat(token).isNotBlank();
        System.out.println("[PASS] 토큰 발급 성공");
    }

    @Test
    @Order(2)
    @DisplayName("단건 이미지 업로드 - 외부 URL → pstatic.net URL 변환")
    void uploadSingleImage() {
        String uploadedUrl = imageClientAdapter.uploadFromUrl(TEST_IMAGE_URL);

        assertThat(uploadedUrl).isNotBlank();
        assertThat(uploadedUrl).contains("pstatic.net");

        System.out.println("[PASS] 단건 이미지 업로드 성공");
        System.out.println("  원본: " + TEST_IMAGE_URL);
        System.out.println("  업로드: " + uploadedUrl);
    }

    @Test
    @Order(3)
    @DisplayName("바이트 배열 직접 업로드 - 프로그래밍 생성 JPEG")
    void uploadFromBytes() throws Exception {
        byte[] jpegBytes = generateTestJpeg(100, 100);

        String uploadedUrl = imageClientAdapter.uploadBytes(
                jpegBytes, "test-generated.jpg", "image/jpeg");

        assertThat(uploadedUrl).isNotBlank();
        assertThat(uploadedUrl).contains("pstatic.net");

        System.out.println("[PASS] 바이트 배열 업로드 성공: " + uploadedUrl);
    }

    @Test
    @Order(4)
    @DisplayName("다건 이미지 업로드 - 생성된 이미지 2건을 한 번에 업로드")
    void uploadMultipleImages() throws Exception {
        byte[] jpeg1 = generateTestJpeg(100, 100);
        byte[] jpeg2 = generateTestJpeg(200, 200);

        // uploadBytes를 2번 호출하여 다건 업로드 동작 검증
        String url1 = imageClientAdapter.uploadBytes(jpeg1, "multi-1.jpg", "image/jpeg");
        String url2 = imageClientAdapter.uploadBytes(jpeg2, "multi-2.jpg", "image/jpeg");

        assertThat(url1).contains("pstatic.net");
        assertThat(url2).contains("pstatic.net");
        assertThat(url1).isNotEqualTo(url2);

        System.out.println("[PASS] 다건 이미지 업로드 성공");
        System.out.println("  [1] " + url1);
        System.out.println("  [2] " + url2);
    }

    /** 테스트용 JPEG 이미지를 프로그래밍으로 생성합니다. */
    private byte[] generateTestJpeg(int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        var g = image.createGraphics();
        g.setColor(java.awt.Color.BLUE);
        g.fillRect(0, 0, width, height);
        g.setColor(java.awt.Color.WHITE);
        g.drawString("TEST", 10, height / 2);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }
}
