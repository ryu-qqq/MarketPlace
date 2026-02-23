package com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.mapper;

import com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.dto.NoticeAnalysisAiResponse;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisSource;
import com.ryuqq.marketplace.domain.productintelligence.vo.NoticeSuggestion;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class NoticeAnalysisAiMapper {

    public String buildUserPrompt(
            ProductNotice productNotice, List<NoticeSuggestion> previousResults) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 현재 고시정보 항목\n");

        for (ProductNoticeEntry entry : productNotice.entries()) {
            String value = entry.fieldValueValue();
            boolean isEmpty = value == null || value.isBlank();
            sb.append("- 필드 ID: ").append(entry.noticeFieldIdValue());
            sb.append(", 값: ").append(isEmpty ? "(비어있음)" : value).append("\n");
        }
        sb.append("\n");

        sb.append("비어있거나 부족한 필드에 대해 적절한 값을 제안하세요.\n");
        sb.append("이미 적절한 값이 있는 필드는 제안하지 않아도 됩니다.\n\n");

        if (!previousResults.isEmpty()) {
            sb.append("## 이전 제안 결과 (참고용)\n");
            for (NoticeSuggestion suggestion : previousResults) {
                sb.append("- 필드 '").append(suggestion.fieldName());
                sb.append("': '").append(suggestion.currentValue());
                sb.append("' → '").append(suggestion.suggestedValue());
                sb.append("' (신뢰도: ").append(suggestion.confidenceValue()).append(")\n");
            }
            sb.append("\n이전 제안을 참고하되, 현재 고시정보 기준으로 새로운 제안을 생성하세요.\n");
        }

        return sb.toString();
    }

    public List<NoticeSuggestion> toNoticeSuggestions(NoticeAnalysisAiResponse response) {
        if (response == null || response.suggestions() == null) {
            return List.of();
        }
        return response.suggestions().stream()
                .map(
                        item ->
                                NoticeSuggestion.of(
                                        item.noticeFieldId(),
                                        item.fieldName(),
                                        item.currentValue(),
                                        item.suggestedValue(),
                                        item.confidence(),
                                        AnalysisSource.LLM_INFERENCE))
                .collect(Collectors.toUnmodifiableList());
    }
}
