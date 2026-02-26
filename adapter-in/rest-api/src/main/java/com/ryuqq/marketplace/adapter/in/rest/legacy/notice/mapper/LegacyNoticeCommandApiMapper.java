package com.ryuqq.marketplace.adapter.in.rest.legacy.notice.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.notice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.application.legacy.notice.dto.command.LegacyUpdateNoticeCommand;
import org.springframework.stereotype.Component;

/** 레거시 상품 고시정보 Request → LegacyUpdateNoticeCommand 변환 매퍼. */
@Component
public class LegacyNoticeCommandApiMapper {

    public LegacyUpdateNoticeCommand toLegacyNoticeCommand(
            long productGroupId, LegacyCreateProductNoticeRequest request) {
        return new LegacyUpdateNoticeCommand(
                productGroupId,
                nullToEmpty(request.material()),
                nullToEmpty(request.color()),
                nullToEmpty(request.size()),
                nullToEmpty(request.maker()),
                nullToEmpty(request.origin()),
                nullToEmpty(request.washingMethod()),
                nullToEmpty(request.yearMonth()),
                nullToEmpty(request.assuranceStandard()),
                nullToEmpty(request.asPhone()));
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
