package com.ryuqq.marketplace.application.legacyshipment.assembler;

import com.ryuqq.marketplace.application.legacyshipment.dto.response.LegacyShipmentCompanyCodeResult;
import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * LegacyShipmentAssembler - 레거시 배송 Assembler.
 *
 * <p>Domain → Result 변환을 담당합니다.
 *
 * <p>APP-ASM-001: 도메인별 구체 Result 클래스 사용.
 */
@Component
public class LegacyShipmentAssembler {

    public LegacyShipmentCompanyCodeResult toCompanyCodeResult(LegacyCommonCode domain) {
        return new LegacyShipmentCompanyCodeResult(
                domain.codeDetailDisplayName(), domain.codeDetail());
    }

    public List<LegacyShipmentCompanyCodeResult> toCompanyCodeResults(
            List<LegacyCommonCode> domains) {
        return domains.stream().map(this::toCompanyCodeResult).toList();
    }
}
