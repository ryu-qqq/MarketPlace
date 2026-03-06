package com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence;

import com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.dto.DescriptionAnalysisAiResponse;
import com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.mapper.DescriptionAnalysisAiMapper;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.DescriptionAnalysisAiClient;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

@Component
@ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")
public class DescriptionAnalysisAiAdapter implements DescriptionAnalysisAiClient {

    private static final Logger log = LoggerFactory.getLogger(DescriptionAnalysisAiAdapter.class);

    private static final int MAX_IMAGES = 5;

    private static final String SYSTEM_PROMPT =
            """
            당신은 이커머스 상품의 상세설명 텍스트와 이미지를 분석하여 구조화된 속성을 추출하는 전문 분석가입니다.

            **분석 대상:**
            1. 텍스트: HTML 상세설명에서 추출한 평문 텍스트
            2. 이미지: 상품 상세 이미지 (상품 정보표, 사이즈 차트, 소재 태그, 라벨 등이 포함될 수 있음)

            **추출할 속성:**
            소재(material), 사이즈(size), 색상(color), 원산지(origin), 제조사(manufacturer),
            무게(weight), 세탁방법(care_instruction), 시즌(season), 성별(gender), 스타일(style) 등.

            **source 분류 (중요):**
            - "text": 텍스트에서 명시적으로 확인한 속성
            - "image": 이미지에서 시각적으로 확인한 속성 (라벨, 태그, 정보표, 사이즈 차트 등)
            - "inference": 텍스트와 이미지를 종합하여 추론한 속성

            **신뢰도 기준:**
            - 텍스트/이미지에서 명시적으로 확인: 0.90~1.0
            - 이미지에서 부분적으로 확인 (흐릿하거나 불완전): 0.70~0.89
            - 종합 추론: 0.50~0.69

            **응답 형식 (JSON):**
            {
              "attributes": [
                {
                  "key": "material",
                  "value": "나일론 100%",
                  "confidence": 0.95,
                  "source": "image",
                  "sourceDetail": "이미지에서 소재 라벨 확인"
                }
              ]
            }
            """;

    private final ChatClient chatClient;
    private final DescriptionAnalysisAiMapper mapper;

    public DescriptionAnalysisAiAdapter(ChatClient chatClient, DescriptionAnalysisAiMapper mapper) {
        this.chatClient = chatClient;
        this.mapper = mapper;
    }

    @Override
    public List<ExtractedAttribute> analyze(
            ProductGroupDescription description, List<ExtractedAttribute> previousResults) {

        List<URL> imageUrls = collectImageUrls(description);

        log.info(
                "Description AI 분석 시작: productGroupId={}, previousResultCount={}, imageCount={}",
                description.productGroupIdValue(),
                previousResults.size(),
                imageUrls.size());

        String userPrompt =
                mapper.buildUserPrompt(description, previousResults, !imageUrls.isEmpty());

        DescriptionAnalysisAiResponse response;
        if (imageUrls.isEmpty()) {
            response =
                    chatClient
                            .prompt()
                            .system(SYSTEM_PROMPT)
                            .user(userPrompt)
                            .call()
                            .entity(DescriptionAnalysisAiResponse.class);
        } else {
            response =
                    chatClient
                            .prompt()
                            .system(SYSTEM_PROMPT)
                            .user(
                                    userSpec -> {
                                        userSpec.text(userPrompt);
                                        for (URL imageUrl : imageUrls) {
                                            userSpec.media(MimeTypeUtils.IMAGE_JPEG, imageUrl);
                                        }
                                    })
                            .call()
                            .entity(DescriptionAnalysisAiResponse.class);
        }

        List<ExtractedAttribute> results = mapper.toExtractedAttributes(response, Instant.now());

        log.info(
                "Description AI 분석 완료: productGroupId={}, extractedCount={}",
                description.productGroupIdValue(),
                results.size());

        return results;
    }

    private List<URL> collectImageUrls(ProductGroupDescription description) {
        List<URL> urls = new ArrayList<>();

        List<DescriptionImage> activeImages =
                description.images().stream()
                        .filter(img -> !img.isDeleted())
                        .sorted(Comparator.comparingInt(DescriptionImage::sortOrder))
                        .limit(MAX_IMAGES)
                        .toList();

        for (DescriptionImage image : activeImages) {
            String urlStr =
                    image.uploadedUrlValue() != null
                            ? image.uploadedUrlValue()
                            : image.originUrlValue();
            try {
                urls.add(URI.create(urlStr).toURL());
            } catch (MalformedURLException | IllegalArgumentException e) {
                log.warn("이미지 URL 파싱 실패, 건너뜀: url={}", urlStr);
            }
        }

        return urls;
    }
}
