package com.ryuqq.marketplace.application.legacyproduct.service.query;

import com.ryuqq.marketplace.application.legacyproduct.assembler.LegacyProductGroupAssembler;
import com.ryuqq.marketplace.application.legacyproduct.dto.composite.LegacyProductGroupDetailBundle;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyProductGroupReadFacade;
import com.ryuqq.marketplace.application.legacyproduct.port.in.query.LegacyProductQueryUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상품 조회 서비스.
 *
 * <p>세토프 PK로 세토프 DB에서 직접 상품 상세를 조회합니다.
 */
@Service
public class LegacyProductQueryService implements LegacyProductQueryUseCase {

    private final LegacyProductGroupReadFacade readFacade;
    private final LegacyProductGroupAssembler assembler;

    public LegacyProductQueryService(
            LegacyProductGroupReadFacade readFacade, LegacyProductGroupAssembler assembler) {
        this.readFacade = readFacade;
        this.assembler = assembler;
    }

    @Override
    @Transactional(readOnly = true)
    public LegacyProductGroupDetailResult execute(long setofProductGroupId) {
        LegacyProductGroupDetailBundle bundle = readFacade.getDetail(setofProductGroupId);
        return assembler.toDetailResult(bundle);
    }
}
