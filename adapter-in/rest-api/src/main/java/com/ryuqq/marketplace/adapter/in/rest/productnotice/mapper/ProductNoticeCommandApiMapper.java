package com.ryuqq.marketplace.adapter.in.rest.productnotice.mapper;

import com.ryuqq.marketplace.adapter.in.rest.productnotice.dto.command.UpdateProductNoticeApiRequest;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import org.springframework.stereotype.Component;

/**
 * ProductNoticeCommandApiMapper - 상품 그룹 고시정보 Command API 변환 매퍼.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ProductNoticeCommandApiMapper {

    /**
     * UpdateProductNoticeApiRequest -> UpdateProductNoticeCommand 변환.
     *
     * @param productGroupId 상품 그룹 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateProductNoticeCommand toCommand(
            Long productGroupId, UpdateProductNoticeApiRequest request) {
        return new UpdateProductNoticeCommand(
                productGroupId,
                request.noticeCategoryId(),
                request.entries().stream()
                        .map(
                                entry ->
                                        new UpdateProductNoticeCommand.NoticeEntryCommand(
                                                entry.noticeFieldId(), entry.fieldValue()))
                        .toList());
    }
}
