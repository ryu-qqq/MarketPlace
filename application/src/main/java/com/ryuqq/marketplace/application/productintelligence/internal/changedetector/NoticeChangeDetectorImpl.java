package com.ryuqq.marketplace.application.productintelligence.internal.changedetector;

import com.ryuqq.marketplace.application.productintelligence.port.out.query.NoticeChangeDetector;
import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeReadManager;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productintelligence.vo.NoticeSuggestion;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Notice 변경 감지 구현체.
 *
 * <p>DB에서 현재 고시정보를 조회하여 이전 분석 결과의 currentValue와 비교합니다. 필드는 카테고리별 고정이므로 값 비교만 수행합니다.
 */
@Component
public class NoticeChangeDetectorImpl implements NoticeChangeDetector {

    private final ProductNoticeReadManager productNoticeReadManager;

    public NoticeChangeDetectorImpl(ProductNoticeReadManager productNoticeReadManager) {
        this.productNoticeReadManager = productNoticeReadManager;
    }

    @Override
    public boolean hasChanged(Long productGroupId, List<NoticeSuggestion> previousResults) {
        if (previousResults.isEmpty()) {
            return true;
        }

        ProductNotice notice =
                productNoticeReadManager.getByProductGroupId(ProductGroupId.of(productGroupId));

        Map<Long, String> currentValueMap =
                notice.entries().stream()
                        .collect(
                                Collectors.toMap(
                                        ProductNoticeEntry::noticeFieldIdValue,
                                        e -> e.fieldValueValue() != null ? e.fieldValueValue() : "",
                                        (a, b) -> a));

        for (NoticeSuggestion suggestion : previousResults) {
            String currentValue = currentValueMap.get(suggestion.noticeFieldId());
            String previousValue =
                    suggestion.currentValue() != null ? suggestion.currentValue() : "";
            if (currentValue == null || !currentValue.equals(previousValue)) {
                return true;
            }
        }

        return false;
    }
}
