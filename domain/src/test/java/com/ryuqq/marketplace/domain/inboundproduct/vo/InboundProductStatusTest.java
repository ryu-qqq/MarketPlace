package com.ryuqq.marketplace.domain.inboundproduct.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundProductStatus 단위 테스트")
class InboundProductStatusTest {

    @Nested
    @DisplayName("상태 확인 메서드 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("RECEIVED 상태는 isReceived()가 true이다")
        void receivedStatusCheck() {
            assertThat(InboundProductStatus.RECEIVED.isReceived()).isTrue();
            assertThat(InboundProductStatus.RECEIVED.isPendingMapping()).isFalse();
            assertThat(InboundProductStatus.RECEIVED.isMapped()).isFalse();
            assertThat(InboundProductStatus.RECEIVED.isConverted()).isFalse();
        }

        @Test
        @DisplayName("PENDING_MAPPING 상태는 isPendingMapping()이 true이다")
        void pendingMappingStatusCheck() {
            assertThat(InboundProductStatus.PENDING_MAPPING.isPendingMapping()).isTrue();
            assertThat(InboundProductStatus.PENDING_MAPPING.isReceived()).isFalse();
        }

        @Test
        @DisplayName("MAPPED 상태는 isMapped()가 true이다")
        void mappedStatusCheck() {
            assertThat(InboundProductStatus.MAPPED.isMapped()).isTrue();
            assertThat(InboundProductStatus.MAPPED.isReceived()).isFalse();
        }

        @Test
        @DisplayName("CONVERTED 상태는 isConverted()가 true이다")
        void convertedStatusCheck() {
            assertThat(InboundProductStatus.CONVERTED.isConverted()).isTrue();
            assertThat(InboundProductStatus.CONVERTED.isMapped()).isFalse();
        }

        @Test
        @DisplayName("LEGACY_IMPORTED 상태는 isLegacyImported()가 true이다")
        void legacyImportedStatusCheck() {
            assertThat(InboundProductStatus.LEGACY_IMPORTED.isLegacyImported()).isTrue();
        }
    }

    @Nested
    @DisplayName("canApplyMapping() 테스트")
    class CanApplyMappingTest {

        @Test
        @DisplayName("RECEIVED 상태는 매핑 적용 가능하다")
        void receivedCanApplyMapping() {
            assertThat(InboundProductStatus.RECEIVED.canApplyMapping()).isTrue();
        }

        @Test
        @DisplayName("PENDING_MAPPING 상태는 매핑 적용 가능하다")
        void pendingMappingCanApplyMapping() {
            assertThat(InboundProductStatus.PENDING_MAPPING.canApplyMapping()).isTrue();
        }

        @Test
        @DisplayName("MAPPED 상태는 매핑 적용 불가하다")
        void mappedCannotApplyMapping() {
            assertThat(InboundProductStatus.MAPPED.canApplyMapping()).isFalse();
        }

        @Test
        @DisplayName("CONVERTED 상태는 매핑 적용 불가하다")
        void convertedCannotApplyMapping() {
            assertThat(InboundProductStatus.CONVERTED.canApplyMapping()).isFalse();
        }

        @Test
        @DisplayName("LEGACY_IMPORTED 상태는 매핑 적용 가능하다 (크롤러 재수신 시)")
        void legacyImportedCanApplyMapping() {
            assertThat(InboundProductStatus.LEGACY_IMPORTED.canApplyMapping()).isTrue();
        }
    }

    @Nested
    @DisplayName("canRouteToInternal() 테스트")
    class CanRouteToInternalTest {

        @Test
        @DisplayName("CONVERTED 상태만 내부 경로 처리 가능하다")
        void onlyConvertedCanRouteToInternal() {
            assertThat(InboundProductStatus.CONVERTED.canRouteToInternal()).isTrue();
            assertThat(InboundProductStatus.RECEIVED.canRouteToInternal()).isFalse();
            assertThat(InboundProductStatus.MAPPED.canRouteToInternal()).isFalse();
            assertThat(InboundProductStatus.LEGACY_IMPORTED.canRouteToInternal()).isFalse();
        }
    }

    @Nested
    @DisplayName("requiresLegacyFallback() 테스트")
    class RequiresLegacyFallbackTest {

        @Test
        @DisplayName("LEGACY_IMPORTED 상태만 레거시 fallback이 필요하다")
        void onlyLegacyImportedRequiresFallback() {
            assertThat(InboundProductStatus.LEGACY_IMPORTED.requiresLegacyFallback()).isTrue();
            assertThat(InboundProductStatus.CONVERTED.requiresLegacyFallback()).isFalse();
            assertThat(InboundProductStatus.RECEIVED.requiresLegacyFallback()).isFalse();
        }
    }

    @Nested
    @DisplayName("fromString() 테스트")
    class FromStringTest {

        @Test
        @DisplayName("유효한 문자열로 상태를 변환한다")
        void parseValidString() {
            assertThat(InboundProductStatus.fromString("RECEIVED"))
                    .isEqualTo(InboundProductStatus.RECEIVED);
            assertThat(InboundProductStatus.fromString("MAPPED"))
                    .isEqualTo(InboundProductStatus.MAPPED);
            assertThat(InboundProductStatus.fromString("CONVERTED"))
                    .isEqualTo(InboundProductStatus.CONVERTED);
        }

        @Test
        @DisplayName("유효하지 않은 문자열이면 예외가 발생한다")
        void parseInvalidString_ThrowsException() {
            assertThatThrownBy(() -> InboundProductStatus.fromString("INVALID"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("각 상태는 한글 설명을 가진다")
        void eachStatusHasKoreanDescription() {
            assertThat(InboundProductStatus.RECEIVED.description()).isEqualTo("수신");
            assertThat(InboundProductStatus.PENDING_MAPPING.description()).isEqualTo("매핑 대기");
            assertThat(InboundProductStatus.MAPPED.description()).isEqualTo("매핑 완료");
            assertThat(InboundProductStatus.CONVERTED.description()).isEqualTo("변환 완료");
            assertThat(InboundProductStatus.LEGACY_IMPORTED.description()).isEqualTo("레거시 임포트");
        }
    }
}
