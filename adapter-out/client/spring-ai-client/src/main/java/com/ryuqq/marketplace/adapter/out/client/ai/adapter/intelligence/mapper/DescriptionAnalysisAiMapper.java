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
            ProductGroupDescription description,
            List<ExtractedAttribute> previousResults,
            boolean hasImages) {
        StringBuilder sb = new StringBuilder();
        sb.append("## 상품 상세설명\n");

        String contentText = description.contentValue();
        if (contentText != null) {
            String plainText = stripHtmlTags(contentText);
            sb.append(plainText).append("\n\n");
        }

        if (hasImages) {
            sb.append("## 첨부 이미지 안내\n");
            sb.append("위에 상품 상세 이미지가 첨부되어 있습니다. ");
            sb.append("이미지에 포함된 상품 정보표, 사이즈 차트, 소재 태그, 라벨 등에서 ");
            sb.append("속성을 추출해주세요.\n\n");
        }

        if (!previousResults.isEmpty()) {
            sb.append("## 이전 분석 결과 (참고용)\n");
            for (ExtractedAttribute attr : previousResults) {
                sb.append("- ").append(attr.key()).append(": ").append(attr.value());
                sb.append(" (신뢰도: ").append(attr.confidenceValue()).append(")\n");
            }
            sb.append("\n이전 결과를 참고하되, 현재 텍스트와 이미지에서 확인 가능한 속성을 우선적으로 추출하세요.\n");
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
                                        resolveSource(item.source()),
                                        item.sourceDetail(),
                                        now))
                .collect(Collectors.toUnmodifiableList());
    }

    private AnalysisSource resolveSource(String source) {
        if (source == null) {
            return AnalysisSource.LLM_INFERENCE;
        }
        return switch (source.toLowerCase()) {
            case "text" -> AnalysisSource.DESCRIPTION_TEXT;
            case "image" -> AnalysisSource.IMAGE_MULTIMODAL;
            default -> AnalysisSource.LLM_INFERENCE;
        };
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
