package com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence;

import com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.dto.NoticeAnalysisAiResponse;
import com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.mapper.NoticeAnalysisAiMapper;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.NoticeAnalysisAiClient;
import com.ryuqq.marketplace.domain.productintelligence.vo.NoticeSuggestion;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "spring.ai.openai", name = "api-key")
public class NoticeAnalysisAiAdapter implements NoticeAnalysisAiClient {

    private static final Logger log = LoggerFactory.getLogger(NoticeAnalysisAiAdapter.class);

    private static final String SYSTEM_PROMPT =
            """
            당신은 이커머스 상품 고시정보를 분석하고 누락된 필드를 보강하는 전문가입니다.
            각 고시정보 필드의 현재 값을 분석하여, 비어있거나 부족한 필드에 대해
            적절한 값을 제안해주세요.
            각 제안에 대해 0.0~1.0 사이의 신뢰도를 부여하세요.
            기존 정보에서 유추 가능한 제안은 0.8 이상, 일반적인 추론은 0.6~0.79로 설정하세요.
            이미 적절한 값이 있는 필드는 제안하지 마세요.
            """;

    private final ChatClient chatClient;
    private final NoticeAnalysisAiMapper mapper;

    public NoticeAnalysisAiAdapter(ChatClient chatClient, NoticeAnalysisAiMapper mapper) {
        this.chatClient = chatClient;
        this.mapper = mapper;
    }

    @Override
    public List<NoticeSuggestion> analyze(
            ProductNotice productNotice, List<NoticeSuggestion> previousResults) {
        log.info(
                "Notice AI 분석 시작: productGroupId={}, previousResultCount={}",
                productNotice.productGroupIdValue(),
                previousResults.size());

        String userPrompt = mapper.buildUserPrompt(productNotice, previousResults);

        NoticeAnalysisAiResponse response =
                chatClient
                        .prompt()
                        .system(SYSTEM_PROMPT)
                        .user(userPrompt)
                        .call()
                        .entity(NoticeAnalysisAiResponse.class);

        List<NoticeSuggestion> results = mapper.toNoticeSuggestions(response);

        log.info(
                "Notice AI 분석 완료: productGroupId={}, suggestionCount={}",
                productNotice.productGroupIdValue(),
                results.size());

        return results;
    }
}
