package com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.mapper;

import com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.dto.OptionAnalysisAiResponse;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisSource;
import com.ryuqq.marketplace.domain.productintelligence.vo.OptionMappingSuggestion;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OptionAnalysisAiMapper {

    public String buildUserPrompt(
            ProductGroup productGroup,
            List<CanonicalOptionGroup> canonicalOptionGroups,
            List<OptionMappingSuggestion> previousResults) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 상품 정보\n");
        sb.append("- 상품명: ").append(productGroup.productGroupNameValue()).append("\n\n");

        sb.append("## 캐노니컬 옵션 (매핑 대상)\n");
        for (CanonicalOptionGroup group : canonicalOptionGroups) {
            sb.append("### 그룹: ").append(group.nameKo());
            sb.append(" [ID: ").append(group.idValue()).append("]\n");
            for (CanonicalOptionValue value : group.values()) {
                sb.append("  - ").append(value.nameKo());
                sb.append(" [ID: ").append(value.idValue()).append("]\n");
            }
            sb.append("\n");
        }

        sb.append("## 셀러 옵션 (매핑 원본)\n");
        for (SellerOptionGroup group : productGroup.sellerOptionGroups()) {
            sb.append("### 옵션그룹 [ID: ").append(group.idValue());
            sb.append(", 이름: ").append(group.optionGroupNameValue()).append("]\n");
            sb.append("옵션값 목록:\n");
            for (SellerOptionValue value : group.optionValues()) {
                sb.append("  - [ID: ").append(value.idValue());
                sb.append(", 이름: ").append(value.optionValueNameValue()).append("]\n");
            }
            sb.append("\n");
        }

        if (!previousResults.isEmpty()) {
            sb.append("## 이전 매핑 결과 (참고용)\n");
            for (OptionMappingSuggestion suggestion : previousResults) {
                sb.append("- 셀러옵션 '").append(suggestion.sellerOptionName());
                sb.append("' → 캐노니컬 '").append(suggestion.suggestedCanonicalValueName());
                sb.append("' (신뢰도: ").append(suggestion.confidenceValue()).append(")\n");
            }
            sb.append("\n이전 매핑을 참고하되, 현재 옵션 목록 기준으로 매핑을 생성하세요.\n");
        }

        return sb.toString();
    }

    public List<OptionMappingSuggestion> toOptionMappingSuggestions(
            OptionAnalysisAiResponse response) {
        if (response == null || response.mappings() == null) {
            return List.of();
        }
        return response.mappings().stream()
                .map(
                        item ->
                                OptionMappingSuggestion.of(
                                        item.sellerOptionGroupId(),
                                        item.sellerOptionValueId(),
                                        item.sellerOptionName(),
                                        item.suggestedCanonicalGroupId(),
                                        item.suggestedCanonicalValueId(),
                                        item.suggestedCanonicalValueName(),
                                        item.confidence(),
                                        AnalysisSource.LLM_INFERENCE))
                .collect(Collectors.toUnmodifiableList());
    }
}
