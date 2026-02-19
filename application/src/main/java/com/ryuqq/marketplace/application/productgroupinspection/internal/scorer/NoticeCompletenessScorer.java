package com.ryuqq.marketplace.application.productgroupinspection.internal.scorer;

import com.ryuqq.marketplace.application.productnotice.manager.ProductNoticeReadManager;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionScoreType;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 고시정보 완성도를 평가하는 Scorer. */
@Component
public class NoticeCompletenessScorer implements InspectionScorer {

    private final ProductNoticeReadManager noticeReadManager;

    public NoticeCompletenessScorer(ProductNoticeReadManager noticeReadManager) {
        this.noticeReadManager = noticeReadManager;
    }

    @Override
    public InspectionScoreType type() {
        return InspectionScoreType.NOTICE_COMPLETENESS;
    }

    @Override
    public int score(Long productGroupId) {
        Optional<ProductNotice> noticeOpt =
                noticeReadManager.findByProductGroupId(ProductGroupId.of(productGroupId));
        if (noticeOpt.isEmpty()) {
            return 0;
        }
        int entryCount = noticeOpt.get().entryCount();
        if (entryCount == 0) {
            return 0;
        }
        return Math.min(100, entryCount * 33);
    }
}
