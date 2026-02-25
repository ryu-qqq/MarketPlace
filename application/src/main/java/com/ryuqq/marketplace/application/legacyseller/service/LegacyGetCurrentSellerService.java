package com.ryuqq.marketplace.application.legacyseller.service;

import com.ryuqq.marketplace.application.legacyseller.dto.response.LegacySellerResult;
import com.ryuqq.marketplace.application.legacyseller.port.in.LegacyGetCurrentSellerUseCase;
import org.springframework.stereotype.Service;

/** 레거시 현재 인증된 셀러 정보 조회 서비스. */
@Service
public class LegacyGetCurrentSellerService implements LegacyGetCurrentSellerUseCase {

    @Override
    public LegacySellerResult execute() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
