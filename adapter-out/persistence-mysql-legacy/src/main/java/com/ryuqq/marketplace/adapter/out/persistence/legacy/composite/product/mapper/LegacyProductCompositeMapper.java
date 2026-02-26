package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductOptionQueryDto;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult.OptionMapping;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB 상품 Composite Mapper.
 *
 * <p>5테이블 조인의 flat 결과를 productId 기준으로 그룹핑하여 {@link LegacyProductCompositeResult} 목록으로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LegacyProductCompositeMapper {

    /**
     * flat projection 행 목록 → 상품 단위 Composite 결과 목록 변환.
     *
     * @param rows 5테이블 조인 결과 행
     * @return productId별로 그룹핑된 Composite 결과
     */
    public List<LegacyProductCompositeResult> toCompositeResults(
            List<LegacyProductOptionQueryDto> rows) {
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }

        Map<Long, List<LegacyProductOptionQueryDto>> grouped =
                rows.stream()
                        .collect(
                                Collectors.groupingBy(
                                        LegacyProductOptionQueryDto::productId,
                                        LinkedHashMap::new,
                                        Collectors.toList()));

        return grouped.entrySet().stream()
                .map(
                        entry -> {
                            LegacyProductOptionQueryDto first = entry.getValue().get(0);
                            List<OptionMapping> mappings =
                                    entry.getValue().stream()
                                            .filter(
                                                    row ->
                                                            row.optionGroupId() != null
                                                                    && row.optionDetailId() != null)
                                            .map(
                                                    row ->
                                                            new OptionMapping(
                                                                    row.optionGroupId(),
                                                                    row.optionDetailId(),
                                                                    row.optionGroupName(),
                                                                    row.optionValue()))
                                            .toList();

                            return new LegacyProductCompositeResult(
                                    first.productId(),
                                    first.productGroupId(),
                                    first.stockQuantity(),
                                    "Y".equals(first.soldOutYn()),
                                    mappings);
                        })
                .toList();
    }
}
