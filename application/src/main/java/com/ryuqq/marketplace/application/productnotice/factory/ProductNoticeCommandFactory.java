package com.ryuqq.marketplace.application.productnotice.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.productnotice.vo.NoticeFieldValue;
import com.ryuqq.marketplace.domain.productnotice.vo.ProductNoticeUpdateData;
import java.util.List;
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
     * 신규 고시정보를 생성합니다.
     *
     * @param command 등록 Command
     * @return 생성된 ProductNotice
     */
    public ProductNotice create(RegisterProductNoticeCommand command) {
        List<ProductNoticeEntry> entries =
                command.entries().stream()
                        .map(
                                entry ->
                                        ProductNoticeEntry.forNew(
                                                NoticeFieldId.of(entry.noticeFieldId()),
                                                NoticeFieldValue.of(entry.fieldValue())))
                        .toList();

        return ProductNotice.forNew(
                ProductGroupId.of(command.productGroupId()),
                NoticeCategoryId.of(command.noticeCategoryId()),
                entries,
                timeProvider.now());
    }

    /**
     * 수정 Command로부터 업데이트 데이터를 생성합니다.
     *
     * @param command 수정 Command
     * @return 고시정보 수정 데이터
     */
    public ProductNoticeUpdateData createUpdateData(UpdateProductNoticeCommand command) {
        List<ProductNoticeEntry> entries =
                command.entries().stream()
                        .map(
                                entry ->
                                        ProductNoticeEntry.forNew(
                                                NoticeFieldId.of(entry.noticeFieldId()),
                                                NoticeFieldValue.of(entry.fieldValue())))
                        .toList();

        return ProductNoticeUpdateData.of(
                NoticeCategoryId.of(command.noticeCategoryId()), entries, timeProvider.now());
    }
}
