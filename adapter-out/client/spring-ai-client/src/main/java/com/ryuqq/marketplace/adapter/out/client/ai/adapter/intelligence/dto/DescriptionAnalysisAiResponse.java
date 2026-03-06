package com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.dto;

import java.util.List;

public record DescriptionAnalysisAiResponse(List<ExtractedAttributeItem> attributes) {

    public record ExtractedAttributeItem(
            String key, String value, double confidence, String source, String sourceDetail) {}
}
