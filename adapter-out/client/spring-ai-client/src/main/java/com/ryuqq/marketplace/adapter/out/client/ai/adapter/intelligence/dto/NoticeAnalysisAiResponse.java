package com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.dto;

import java.util.List;

public record NoticeAnalysisAiResponse(List<NoticeSuggestionItem> suggestions) {

    public record NoticeSuggestionItem(
            long noticeFieldId,
            String fieldName,
            String currentValue,
            String suggestedValue,
            double confidence) {}
}
