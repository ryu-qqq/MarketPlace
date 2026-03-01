package com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence;

import com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.dto.DescriptionAnalysisAiResponse;
import com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.mapper.DescriptionAnalysisAiMapper;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.DescriptionAnalysisAiClient;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")
public class DescriptionAnalysisAiAdapter implements DescriptionAnalysisAiClient {

    private static final Logger log = LoggerFactory.getLogger(DescriptionAnalysisAiAdapter.class);

    private static final String SYSTEM_PROMPT =
            """
            당신은 이커머스 상품 상세설명에서 구조화된 속성을 추출하는 전문 분석가입니다.
            HTML 상세설명 텍스트를 분석하여 다음 속성을 추출하세요:
            소재(material), 사이즈(size), 색상(color), 원산지(origin), 제조사(manufacturer),
            무게(weight), 세탁방법(care_instruction), 시즌(season) 등.
            각 속성에 대해 0.0~1.0 사이의 신뢰도를 부여하세요.
            텍스트에서 명시적으로 확인 가능한 속성은 0.9 이상, 추론된 속성은 0.7~0.89로 설정하세요.
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
        log.info(
                "Description AI 분석 시작: productGroupId={}, previousResultCount={}",
                description.productGroupIdValue(),
                previousResults.size());

        String userPrompt = mapper.buildUserPrompt(description, previousResults);

        DescriptionAnalysisAiResponse response =
                chatClient
                        .prompt()
                        .system(SYSTEM_PROMPT)
                        .user(userPrompt)
                        .call()
                        .entity(DescriptionAnalysisAiResponse.class);

        List<ExtractedAttribute> results = mapper.toExtractedAttributes(response, Instant.now());

        log.info(
                "Description AI 분석 완료: productGroupId={}, extractedCount={}",
                description.productGroupIdValue(),
                results.size());

        return results;
    }
}
