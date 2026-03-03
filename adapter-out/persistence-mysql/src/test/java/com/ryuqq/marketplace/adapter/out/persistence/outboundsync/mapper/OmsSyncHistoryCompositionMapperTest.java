package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.SyncHistoryCompositeDtoFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.composite.SyncHistoryCompositeDto;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OmsSyncHistoryCompositionMapperTest - 연동 이력 Composition 매퍼 단위 테스트.
 *
 * <p>SyncHistoryCompositeDto → SyncHistoryListResult 변환 로직 검증.
 */
@Tag("unit")
@DisplayName("OmsSyncHistoryCompositionMapper 단위 테스트")
class OmsSyncHistoryCompositionMapperTest {

    private OmsSyncHistoryCompositionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OmsSyncHistoryCompositionMapper();
    }

    // ========================================================================
    // 1. toResults 테스트
    // ========================================================================

    @Nested
    @DisplayName("toResults 메서드 테스트")
    class ToResultsTest {

        @Test
        @DisplayName("COMPLETED 상태 composites를 올바르게 변환합니다")
        void toResults_WithCompletedComposites_ReturnsCorrectResults() {
            // given
            List<SyncHistoryCompositeDto> composites =
                    List.of(SyncHistoryCompositeDtoFixtures.completedDto(1L));

            // when
            List<SyncHistoryListResult> results = mapper.toResults(composites);

            // then
            assertThat(results).hasSize(1);
            SyncHistoryListResult result = results.get(0);
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.shopName())
                    .isEqualTo(SyncHistoryCompositeDtoFixtures.DEFAULT_SHOP_NAME);
            assertThat(result.accountId())
                    .isEqualTo(SyncHistoryCompositeDtoFixtures.DEFAULT_ACCOUNT_ID);
            assertThat(result.presetName()).isEqualTo("디폴트 프리셋");
            assertThat(result.status()).isEqualTo("COMPLETED");
            assertThat(result.statusLabel()).isNotBlank();
            assertThat(result.externalProductId())
                    .isEqualTo(SyncHistoryCompositeDtoFixtures.DEFAULT_EXTERNAL_PRODUCT_ID);
        }

        @Test
        @DisplayName("FAILED 상태 composites를 올바르게 변환합니다")
        void toResults_WithFailedComposites_ReturnsCorrectResults() {
            // given
            List<SyncHistoryCompositeDto> composites =
                    List.of(SyncHistoryCompositeDtoFixtures.failedDto(2L));

            // when
            List<SyncHistoryListResult> results = mapper.toResults(composites);

            // then
            assertThat(results).hasSize(1);
            SyncHistoryListResult result = results.get(0);
            assertThat(result.id()).isEqualTo(2L);
            assertThat(result.status()).isEqualTo("FAILED");
            assertThat(result.retryCount()).isEqualTo(3);
            assertThat(result.errorMessage()).isEqualTo("외부 채널 연동 최대 재시도 초과");
            assertThat(result.externalProductId()).isNull();
        }

        @Test
        @DisplayName("PENDING 상태 composites를 올바르게 변환합니다")
        void toResults_WithPendingComposites_ReturnsCorrectResults() {
            // given
            List<SyncHistoryCompositeDto> composites =
                    List.of(SyncHistoryCompositeDtoFixtures.pendingDto(3L));

            // when
            List<SyncHistoryListResult> results = mapper.toResults(composites);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).status()).isEqualTo("PENDING");
            assertThat(results.get(0).completedAt()).isNull();
        }

        @Test
        @DisplayName("shopName이 null이면 빈 문자열로 변환합니다")
        void toResults_WithNullShopName_ReturnsEmptyString() {
            // given
            List<SyncHistoryCompositeDto> composites =
                    List.of(SyncHistoryCompositeDtoFixtures.dtoWithNullShopInfo(1L));

            // when
            List<SyncHistoryListResult> results = mapper.toResults(composites);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).shopName()).isEmpty();
            assertThat(results.get(0).accountId()).isEmpty();
        }

        @Test
        @DisplayName("빈 composites 목록 입력 시 빈 결과를 반환합니다")
        void toResults_WithEmptyComposites_ReturnsEmptyList() {
            // when
            List<SyncHistoryListResult> results = mapper.toResults(List.of());

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("여러 composites를 순서대로 변환합니다")
        void toResults_WithMultipleComposites_PreservesOrder() {
            // given
            List<SyncHistoryCompositeDto> composites =
                    SyncHistoryCompositeDtoFixtures.completedDtoList(3);

            // when
            List<SyncHistoryListResult> results = mapper.toResults(composites);

            // then
            assertThat(results).hasSize(3);
        }

        @Test
        @DisplayName("status가 null이면 빈 statusLabel을 반환합니다")
        void toResults_WithNullStatus_ReturnsEmptyStatusLabel() {
            // given
            SyncHistoryCompositeDto dtoWithNullStatus =
                    new SyncHistoryCompositeDto(
                            1L, "샵", "account", null, 0, null, null, java.time.Instant.now(), null);
            List<SyncHistoryCompositeDto> composites = List.of(dtoWithNullStatus);

            // when
            List<SyncHistoryListResult> results = mapper.toResults(composites);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).status()).isEmpty();
            assertThat(results.get(0).statusLabel()).isEmpty();
        }

        @Test
        @DisplayName("알 수 없는 status는 원본 값을 statusLabel로 반환합니다")
        void toResults_WithUnknownStatus_ReturnsRawStatusAsLabel() {
            // given
            SyncHistoryCompositeDto dtoWithUnknownStatus =
                    new SyncHistoryCompositeDto(
                            1L,
                            "샵",
                            "account",
                            "UNKNOWN_STATUS",
                            0,
                            null,
                            null,
                            java.time.Instant.now(),
                            null);
            List<SyncHistoryCompositeDto> composites = List.of(dtoWithUnknownStatus);

            // when
            List<SyncHistoryListResult> results = mapper.toResults(composites);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).statusLabel()).isEqualTo("UNKNOWN_STATUS");
        }
    }
}
