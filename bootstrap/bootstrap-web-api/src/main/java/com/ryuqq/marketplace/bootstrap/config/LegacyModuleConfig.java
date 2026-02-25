package com.ryuqq.marketplace.bootstrap.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Legacy 모듈 조건부 활성화 설정.
 *
 * <p>세토프(레거시) DB 연동 모듈을 persistence.legacy.enabled=true일 때만 활성화합니다. 3개 레이어(adapter-out,
 * application, adapter-in)의 legacy 패키지를 한 번에 스캔합니다.
 *
 * <p>MarketPlaceApplication의 기본 ComponentScan에서 legacy 패키지가 제외되어 있으므로, 이 Config가 비활성화되면 legacy 빈은
 * 전혀 등록되지 않습니다.
 */
@Configuration
@ConditionalOnProperty(name = "persistence.legacy.enabled", havingValue = "true")
@ComponentScan(
        basePackages = {
            "com.ryuqq.marketplace.adapter.out.persistence.legacy",
            "com.ryuqq.marketplace.application.legacyproduct",
            "com.ryuqq.marketplace.application.legacyauth",
            "com.ryuqq.marketplace.application.legacyseller",
            "com.ryuqq.marketplace.application.legacyshipment",
            "com.ryuqq.marketplace.adapter.in.rest.legacy"
        })
public class LegacyModuleConfig {}
