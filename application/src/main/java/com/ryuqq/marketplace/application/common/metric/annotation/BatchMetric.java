package com.ryuqq.marketplace.application.common.metric.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 배치 처리 메서드에 메트릭을 수집하는 어노테이션.
 *
 * <p>생성되는 메트릭:
 *
 * <ul>
 *   <li>{@code marketplace.{value}_duration_seconds} — 실행 시간 (Timer)
 *   <li>{@code marketplace.{value}_total} — 실행 횟수 (Counter)
 *   <li>{@code marketplace.{value}_items_total} — 배치 항목 수 (Counter, SchedulerBatchProcessingResult
 *       반환 시)
 *   <li>{@code marketplace.{value}_errors_total} — 에러 횟수 (Counter)
 * </ul>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BatchMetric {

    /** 메트릭 이름. */
    String value();

    /** 배치 카테고리 (태그로 사용). */
    String category();
}
