package com.ryuqq.marketplace.domain.productintelligence.vo;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("IntelligenceOutboxIdempotencyKey 단위 테스트")
class IntelligenceOutboxIdempotencyKeyTest {

    @Nested
    @DisplayName("generate() - 신규 멱등키 생성")
    class GenerateTest {

        @Test
        @DisplayName("productGroupId와 createdAt으로 멱등키를 생성한다")
        void generateWithValidParameters() {
            Long productGroupId = 100L;
            Instant createdAt = Instant.ofEpochMilli(1740556800000L);

            IntelligenceOutboxIdempotencyKey key =
                    IntelligenceOutboxIdempotencyKey.generate(productGroupId, createdAt);

            assertThat(key.value()).isEqualTo("PI:100:1740556800000");
        }

        @Test
        @DisplayName("생성된 멱등키는 PI: 접두사를 가진다")
        void generatedKeyHasPiPrefix() {
            IntelligenceOutboxIdempotencyKey key =
                    IntelligenceOutboxIdempotencyKey.generate(100L, Instant.now());

            assertThat(key.value()).startsWith("PI:");
        }

        @Test
        @DisplayName("productGroupId가 null이면 예외가 발생한다")
        void generateWithNullProductGroupId_ThrowsException() {
            assertThatThrownBy(() -> IntelligenceOutboxIdempotencyKey.generate(null, Instant.now()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("createdAt이 null이면 예외가 발생한다")
        void generateWithNullCreatedAt_ThrowsException() {
            assertThatThrownBy(() -> IntelligenceOutboxIdempotencyKey.generate(100L, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("of() - 기존 값으로 재구성")
    class OfTest {

        @Test
        @DisplayName("유효한 PI: 형식의 값으로 재구성한다")
        void reconstitutionWithValidValue() {
            String value = "PI:100:1740556800000";

            IntelligenceOutboxIdempotencyKey key = IntelligenceOutboxIdempotencyKey.of(value);

            assertThat(key.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("null 값이면 예외가 발생한다")
        void ofWithNull_ThrowsException() {
            assertThatThrownBy(() -> IntelligenceOutboxIdempotencyKey.of(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("PI: 접두사 없이 잘못된 형식이면 예외가 발생한다")
        void ofWithInvalidFormat_ThrowsException() {
            assertThatThrownBy(() -> IntelligenceOutboxIdempotencyKey.of("INVALID:100:123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("잘못된 멱등키 형식");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void ofWithEmptyString_ThrowsException() {
            assertThatThrownBy(() -> IntelligenceOutboxIdempotencyKey.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("동등성 검증")
    class EqualityTest {

        @Test
        @DisplayName("동일한 값의 멱등키는 같다")
        void sameValuesAreEqual() {
            String value = "PI:100:1740556800000";

            IntelligenceOutboxIdempotencyKey key1 = IntelligenceOutboxIdempotencyKey.of(value);
            IntelligenceOutboxIdempotencyKey key2 = IntelligenceOutboxIdempotencyKey.of(value);

            assertThat(key1).isEqualTo(key2);
            assertThat(key1.hashCode()).isEqualTo(key2.hashCode());
        }

        @Test
        @DisplayName("다른 값의 멱등키는 같지 않다")
        void differentValuesAreNotEqual() {
            IntelligenceOutboxIdempotencyKey key1 =
                    IntelligenceOutboxIdempotencyKey.of("PI:100:1740556800000");
            IntelligenceOutboxIdempotencyKey key2 =
                    IntelligenceOutboxIdempotencyKey.of("PI:200:1740556800000");

            assertThat(key1).isNotEqualTo(key2);
        }
    }

    @Nested
    @DisplayName("toString() 검증")
    class ToStringTest {

        @Test
        @DisplayName("toString은 value를 반환한다")
        void toStringReturnsValue() {
            String value = "PI:100:1740556800000";
            IntelligenceOutboxIdempotencyKey key = IntelligenceOutboxIdempotencyKey.of(value);

            assertThat(key.toString()).isEqualTo(value);
        }
    }
}
