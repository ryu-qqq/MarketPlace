package com.ryuqq.marketplace.application.legacy.productnotice.port.out.command;

import java.util.Map;

/**
 * 레거시 상품 고시정보 저장 Port.
 *
 * <p>flat Map(fieldCode → value)을 받아서 luxurydb product_notice 테이블에 저장합니다.
 */
public interface LegacyProductNoticeCommandPort {

    void persist(long productGroupId, Map<String, String> flatFields);
}
