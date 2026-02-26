package com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.mapper;

import com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.dto.DescriptionAnalysisAiResponse;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisSource;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class DescriptionAnalysisAiMapper {

    public String buildUserPrompt(
            ProductGroupDescription description, List<ExtractedAttribute> previousResults) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 상품 상세설명\n");

        String contentText = description.contentValue();
        if (contentText != null) {
            String plainText = stripHtmlTags(contentText);
            sb.append(plainText).append("\n\n");
        }

        if (!previousResults.isEmpty()) {
            sb.append("## 이전 분석 결과 (참고용)\n");
            for (ExtractedAttribute attr : previousResults) {
                sb.append("- ").append(attr.key()).append(": ").append(attr.value());
                sb.append(" (신뢰도: ").append(attr.confidenceValue()).append(")\n");
            }
            sb.append("\n이전 결과를 참고하되, 현재 텍스트에서 확인 가능한 속성을 우선적으로 추출하세요.\n");
        }

        return sb.toString();
    }

    public List<ExtractedAttribute> toExtractedAttributes(
            DescriptionAnalysisAiResponse response, Instant now) {
        if (response == null || response.attributes() == null) {
            return List.of();
        }
        return response.attributes().stream()
                .map(
                        item ->
                                ExtractedAttribute.of(
                                        item.key(),
                                        item.value(),
                                        item.confidence(),
                                        AnalysisSource.LLM_INFERENCE,
                                        item.sourceDetail(),
                                        now))
                .collect(Collectors.toUnmodifiableList());
    }

    private String stripHtmlTags(String html) {
        return html.replaceAll("<[^>]*>", " ")
                .replaceAll("&nbsp;", " ")
                .replaceAll("&amp;", "&")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
