package com.ryuqq.marketplace.adapter.out.persistence.common.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UuidIdGeneratorAdapterTest - UUID ID 생성 Adapter 단위 테스트.
 *
 * <p>IdGeneratorPort 구현체가 올바른 UUID를 생성하는지 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UuidIdGeneratorAdapter 단위 테스트")
class UuidIdGeneratorAdapterTest {

    private final UuidIdGeneratorAdapter adapter = new UuidIdGeneratorAdapter();

    // ========================================================================
    // 1. generate 테스트
    // ========================================================================

    @Nested
    @DisplayName("generate 메서드 테스트")
    class GenerateTest {

        @Test
        @DisplayName("생성된 ID는 null이 아닙니다")
        void generate_ReturnsNonNullId() {
            // when
            String id = adapter.generate();

            // then
            assertThat(id).isNotNull();
        }

        @Test
        @DisplayName("생성된 ID는 빈 문자열이 아닙니다")
        void generate_ReturnsNonEmptyId() {
            // when
            String id = adapter.generate();

            // then
            assertThat(id).isNotBlank();
        }

        @Test
        @DisplayName("생성된 ID는 UUID 형식입니다")
        void generate_ReturnsUuidFormat() {
            // when
            String id = adapter.generate();

            // then
            assertThat(id).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        }

        @Test
        @DisplayName("여러 번 호출 시 서로 다른 고유한 ID를 생성합니다")
        void generate_MultipleCalls_ReturnsUniqueIds() {
            // given
            int count = 100;
            Set<String> generatedIds = new HashSet<>();

            // when
            for (int i = 0; i < count; i++) {
                generatedIds.add(adapter.generate());
            }

            // then
            assertThat(generatedIds).hasSize(count);
        }
    }
}
