package com.ryuqq.marketplace.adapter.out.client.ai.adapter.intelligence.dto;

import java.util.List;

public record OptionAnalysisAiResponse(List<OptionMappingItem> mappings) {

    public record OptionMappingItem(
            long sellerOptionGroupId,
            long sellerOptionValueId,
            String sellerOptionName,
            long suggestedCanonicalGroupId,
            long suggestedCanonicalValueId,
            String suggestedCanonicalValueName,
            double confidence) {}
}
