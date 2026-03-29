package com.ryuqq.marketplace.adapter.in.rest.legacy.common.config;

import java.time.format.DateTimeFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 레거시 API 날짜 포맷 설정.
 *
 * <p>쿼리 파라미터의 LocalDateTime 바인딩 시 "yyyy-MM-dd HH:mm:ss" 포맷을 지원합니다. record DTO의
 * {@code @DateTimeFormat}이 Spring에서 인식되지 않는 문제를 글로벌 포매터로 해결합니다.
 */
@Configuration
public class LegacyDateTimeConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        registrar.registerFormatters(registry);
    }
}
