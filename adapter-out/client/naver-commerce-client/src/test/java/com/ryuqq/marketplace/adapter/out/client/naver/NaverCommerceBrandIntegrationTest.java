package com.ryuqq.marketplace.adapter.out.client.naver;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverCommerceBrand;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

/**
 * 네이버 커머스 브랜드 전체 수집 일회성 테스트.
 *
 * <p>한글 자음/모음 + 알파벳 + 숫자로 검색하여 모든 브랜드를 CSV로 덤프합니다.
 */
@Tag("external-integration")
@SpringBootTest(classes = NaverCommerceTestApplication.class)
@ActiveProfiles("naver-test")
class NaverCommerceBrandIntegrationTest {

    @Autowired private RestClient naverCommerceRestClient;
    @Autowired private NaverCommerceTokenManager tokenManager;

    @Test
    @DisplayName("네이버 브랜드 전수 검색 → CSV 덤프")
    void fetchAllBrandsAndDumpToCsv() throws IOException {
        String token = tokenManager.getAccessToken();

        // id → brand (중복 제거용)
        Map<Long, NaverCommerceBrand> allBrands = new LinkedHashMap<>();

        // 한글 자음 (ㄱ~ㅎ)
        String[] koreanConsonants = {
            "ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
        };

        // 한글 가나다 시작 음절
        String[] koreanSyllables = {
            "가", "나", "다", "라", "마", "바", "사", "아", "자", "차", "카", "타", "파", "하",
            "개", "내", "대", "래", "매", "배", "새", "애", "재", "채", "캐", "태", "패", "해",
            "고", "노", "도", "로", "모", "보", "소", "오", "조", "초", "코", "토", "포", "호",
            "구", "누", "두", "루", "무", "부", "수", "우", "주", "추", "쿠", "투", "푸", "후",
            "그", "느", "드", "르", "므", "브", "스", "으", "즈", "츠", "크", "트", "프", "흐",
            "기", "니", "디", "리", "미", "비", "시", "이", "지", "치", "키", "티", "피", "히"
        };

        // 알파벳 A~Z
        String[] alphabets = new String[26];
        for (int i = 0; i < 26; i++) {
            alphabets[i] = String.valueOf((char) ('A' + i));
        }

        // 숫자 0~9
        String[] digits = new String[10];
        for (int i = 0; i < 10; i++) {
            digits[i] = String.valueOf(i);
        }

        // 모든 검색어 합치기
        String[][] allSearchTerms = {koreanConsonants, koreanSyllables, alphabets, digits};

        for (String[] terms : allSearchTerms) {
            for (String term : terms) {
                try {
                    List<NaverCommerceBrand> brands =
                            naverCommerceRestClient
                                    .get()
                                    .uri("/v1/product-brands?name={name}", term)
                                    .header("Authorization", "Bearer " + token)
                                    .retrieve()
                                    .body(new ParameterizedTypeReference<>() {});

                    if (brands != null) {
                        for (NaverCommerceBrand b : brands) {
                            allBrands.put(b.id(), b);
                        }
                        System.out.println(
                                "[검색] '"
                                        + term
                                        + "' → "
                                        + brands.size()
                                        + "건 (누적: "
                                        + allBrands.size()
                                        + ")");
                    }

                    // API rate limit 방지
                    Thread.sleep(100);
                } catch (Exception e) {
                    System.err.println("[오류] '" + term + "' 검색 실패: " + e.getMessage());
                }
            }
        }

        // CSV 파일 저장
        String csvPath = "naver_brands.csv";
        try (FileWriter writer = new FileWriter(csvPath)) {
            writer.write("id,name\n");
            for (NaverCommerceBrand brand : allBrands.values()) {
                String safeName = brand.name().replace("\"", "\"\"");
                writer.write(brand.id() + ",\"" + safeName + "\"\n");
            }
        }

        System.out.println("\n=== 결과 ===");
        System.out.println("총 브랜드 수: " + allBrands.size());
        System.out.println("CSV 저장: " + csvPath);

        assertThat(allBrands).isNotEmpty();
    }

    @Test
    @DisplayName("특정 브랜드 검색 테스트 - 디즈니/Disney")
    void searchSpecificBrand() {
        String token = tokenManager.getAccessToken();

        String[] searchTerms = {"디즈니", "Disney", "disney", "DISNEY"};

        for (String term : searchTerms) {
            List<NaverCommerceBrand> brands =
                    naverCommerceRestClient
                            .get()
                            .uri("/v1/product-brands?name={name}", term)
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .body(new ParameterizedTypeReference<>() {});

            System.out.println(
                    "[검색] '" + term + "' → " + (brands != null ? brands.size() : 0) + "건");
            if (brands != null) {
                for (NaverCommerceBrand b : brands) {
                    System.out.println("  - id=" + b.id() + ", name=" + b.name());
                }
            }
        }
    }
}
