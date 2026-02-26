package com.ryuqq.marketplace.application.common.metric.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 외부 시스템 호출 메서드에 메트릭을 수집하는 어노테이션.
 *
 * <p>생성되는 메트릭:
 *
 * <ul>
 *   <li>{@code marketplace.outbound_client_duration_seconds} — 호출 시간 (Timer)
 *   <li>{@code marketplace.outbound_client_total} — 호출 횟수 (Counter)
 *   <li>{@code marketplace.outbound_client_errors_total} — 에러 횟수 (Counter)
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OutboundClientMetric {

    /** 외부 시스템명 (예: s3, sqs, authhub). */
    String system();

    /** 작업 유형 (예: put, get, publish). */
    String operation();
}
