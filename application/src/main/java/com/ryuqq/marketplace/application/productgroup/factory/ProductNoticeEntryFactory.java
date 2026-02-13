package com.ryuqq.marketplace.application.productgroup.factory;

import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.productnotice.vo.NoticeFieldValue;
import com.ryuqq.marketplace.domain.productnotice.vo.ProductNoticeEntries;
import java.util.List;
import org.springframework.stereotype.Component;

/** ProductNoticeEntry 생성 서브 팩토리. */
@Component
public class ProductNoticeEntryFactory {

    public ProductNoticeEntries create(NoticeData noticeData) {
        NoticeCategoryId noticeCategoryId = NoticeCategoryId.of(noticeData.noticeCategoryId());

        List<ProductNoticeEntry> entries =
                noticeData.entries().stream()
                        .map(
                                data ->
                                        ProductNoticeEntry.forNew(
                                                NoticeFieldId.of(data.noticeFieldId()),
                                                NoticeFieldValue.of(data.fieldValue())))
                        .toList();

        return ProductNoticeEntries.of(noticeCategoryId, entries);
    }

    public record NoticeData(long noticeCategoryId, List<NoticeEntryData> entries) {}

    public record NoticeEntryData(long noticeFieldId, String fieldValue) {}
}
