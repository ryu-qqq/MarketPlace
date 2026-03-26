package com.ryuqq.marketplace.adapter.in.rest.legacy;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * REST API Legacy 모듈 테스트용 Spring Boot Application 설정.
 *
 * <p>adapter-in/rest-api-legacy 모듈의 슬라이스 테스트(@WebMvcTest 등)에서 사용됩니다.
 *
 * <p>@WebMvcTest가 직접 컴포넌트 스캔을 관리하므로, 별도의 @ComponentScan 설정은 하지 않습니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@SpringBootApplication
@SuppressWarnings("PMD.TestClassWithoutTestCases")
class TestWebMvcConfig {}
