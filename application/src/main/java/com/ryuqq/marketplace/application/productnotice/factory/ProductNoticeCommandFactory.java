package com.ryuqq.marketplace.application.productnotice.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.productnotice.vo.NoticeFieldValue;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * ProductNotice Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider는 Factory에서만 사용합니다.
 */
@Component
public class ProductNoticeCommandFactory {

    private final TimeProvider timeProvider;

    public ProductNoticeCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 고시정보를 생성하거나 기존 고시정보를 업데이트합니다.
     *
     * @param command 수정 Command
     * @param existingOpt 기존 고시정보 (Optional)
     * @return 생성 또는 수정된 ProductNotice
     */
    public ProductNotice createOrUpdateNotice(
            UpdateProductNoticeCommand command, Optional<ProductNotice> existingOpt) {

        List<ProductNoticeEntry> entries =
                command.entries().stream()
                        .map(
                                entry ->
                                        ProductNoticeEntry.forNew(
                                                NoticeFieldId.of(entry.noticeFieldId()),
                                                NoticeFieldValue.of(entry.fieldValue())))
                        .toList();

        if (existingOpt.isPresent()) {
            ProductNotice existing = existingOpt.get();
            existing.replaceEntries(entries, timeProvider.now());
            return existing;
        } else {
            return ProductNotice.forNew(
                    ProductGroupId.of(command.productGroupId()),
                    NoticeCategoryId.of(command.noticeCategoryId()),
                    entries,
                    timeProvider.now());
        }
    }
}
