package com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence;

import com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.dto.OptionAnalysisAiResponse;
import com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.mapper.OptionAnalysisAiMapper;
import com.ryuqq.marketplace.application.productintelligence.port.out.client.OptionAnalysisAiClient;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productintelligence.vo.OptionMappingSuggestion;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "spring.ai.anthropic", name = "api-key")
public class OptionAnalysisAiAdapter implements OptionAnalysisAiClient {

    private static final Logger log = LoggerFactory.getLogger(OptionAnalysisAiAdapter.class);

    private static final String SYSTEM_PROMPT =
            """
            당신은 이커머스 상품 옵션을 표준화된(캐노니컬) 옵션에 매핑하는 전문가입니다.
            셀러가 자유롭게 입력한 옵션 그룹명과 옵션값을 분석하여,
            제공된 캐노니컬 옵션 목록에서 가장 적합한 그룹과 값을 찾아 매핑해주세요.
            반드시 제공된 캐노니컬 옵션 ID를 사용하세요. 존재하지 않는 ID를 생성하지 마세요.
            예: 셀러 "컬러/빨강색" → 캐노니컬 "색상/레드"
            각 매핑에 대해 0.0~1.0 사이의 신뢰도를 부여하세요.
            정확한 매핑은 0.9 이상, 추론이 필요한 매핑은 0.7~0.89로 설정하세요.
            캐노니컬 목록에 적합한 매핑 대상이 없는 옵션은 결과에 포함하지 마세요.
            """;

    private final ChatClient chatClient;
    private final OptionAnalysisAiMapper mapper;

    public OptionAnalysisAiAdapter(ChatClient chatClient, OptionAnalysisAiMapper mapper) {
        this.chatClient = chatClient;
        this.mapper = mapper;
    }

    @Override
    public List<OptionMappingSuggestion> analyze(
            ProductGroup productGroup,
            List<CanonicalOptionGroup> canonicalOptionGroups,
            List<OptionMappingSuggestion> previousResults) {
        log.info(
                "Option AI 분석 시작: productGroupId={}, canonicalGroupCount={},"
                        + " previousResultCount={}",
                productGroup.idValue(),
                canonicalOptionGroups.size(),
                previousResults.size());

        String userPrompt =
                mapper.buildUserPrompt(productGroup, canonicalOptionGroups, previousResults);

        OptionAnalysisAiResponse response =
                chatClient
                        .prompt()
                        .system(SYSTEM_PROMPT)
                        .user(userPrompt)
                        .call()
                        .entity(OptionAnalysisAiResponse.class);

        List<OptionMappingSuggestion> results = mapper.toOptionMappingSuggestions(response);

        log.info(
                "Option AI 분석 완료: productGroupId={}, mappingCount={}",
                productGroup.idValue(),
                results.size());

        return results;
    }
}
