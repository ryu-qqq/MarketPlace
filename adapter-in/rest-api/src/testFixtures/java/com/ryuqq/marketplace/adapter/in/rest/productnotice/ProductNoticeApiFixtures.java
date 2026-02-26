package com.ryuqq.marketplace.adapter.in.rest.productnotice;

import com.ryuqq.marketplace.adapter.in.rest.productnotice.dto.command.UpdateProductNoticeApiRequest;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import java.util.List;

/**
 * ProductNotice API 테스트 Fixtures.
 *
 * <p>ProductNotice REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ProductNoticeApiFixtures {

    private ProductNoticeApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 1L;
    public static final Long DEFAULT_NOTICE_CATEGORY_ID = 1L;
    public static final Long DEFAULT_NOTICE_FIELD_ID_1 = 101L;
    public static final Long DEFAULT_NOTICE_FIELD_ID_2 = 102L;

    // ===== UpdateProductNoticeApiRequest =====

    public static UpdateProductNoticeApiRequest updateRequest() {
        List<UpdateProductNoticeApiRequest.NoticeEntryRequest> entries =
                List.of(
                        new UpdateProductNoticeApiRequest.NoticeEntryRequest(
                                DEFAULT_NOTICE_FIELD_ID_1, "제조사"),
                        new UpdateProductNoticeApiRequest.NoticeEntryRequest(
                                DEFAULT_NOTICE_FIELD_ID_2, "한국"));
        return new UpdateProductNoticeApiRequest(DEFAULT_NOTICE_CATEGORY_ID, entries);
    }

    public static UpdateProductNoticeApiRequest updateRequest(
            Long noticeCategoryId, List<UpdateProductNoticeApiRequest.NoticeEntryRequest> entries) {
        return new UpdateProductNoticeApiRequest(noticeCategoryId, entries);
    }

    public static UpdateProductNoticeApiRequest updateRequestSingleEntry() {
        List<UpdateProductNoticeApiRequest.NoticeEntryRequest> entries =
                List.of(
                        new UpdateProductNoticeApiRequest.NoticeEntryRequest(
                                DEFAULT_NOTICE_FIELD_ID_1, "단일 항목 값"));
        return new UpdateProductNoticeApiRequest(DEFAULT_NOTICE_CATEGORY_ID, entries);
    }

    // ===== UpdateProductNoticeCommand =====

    public static UpdateProductNoticeCommand updateCommand(Long productGroupId) {
        return new UpdateProductNoticeCommand(
                productGroupId,
                DEFAULT_NOTICE_CATEGORY_ID,
                List.of(
                        new UpdateProductNoticeCommand.NoticeEntryCommand(
                                DEFAULT_NOTICE_FIELD_ID_1, "제조사"),
                        new UpdateProductNoticeCommand.NoticeEntryCommand(
                                DEFAULT_NOTICE_FIELD_ID_2, "한국")));
    }
}
