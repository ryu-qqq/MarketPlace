package com.ryuqq.marketplace.application.outboundproduct.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.outboundproduct.OmsProductQueryFixtures;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.SyncHistorySearchParams;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySearchCriteria;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OmsSyncHistoryQueryFactory 단위 테스트")
class OmsSyncHistoryQueryFactoryTest {

    private OmsSyncHistoryQueryFactory sut;

    @BeforeEach
    void setUp() {
        sut = new OmsSyncHistoryQueryFactory();
    }

    @Nested
    @DisplayName("createCriteria() - SyncHistorySearchCriteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("상품 그룹 ID와 기본 파라미터로 Criteria를 생성한다")
        void createCriteria_DefaultParams_CreatesCriteria() {
            // given
            long productGroupId = 100L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(productGroupId);

            // when
            SyncHistorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productGroupId()).isEqualTo(productGroupId);
            assertThat(result.statusFilter()).isNull();
            assertThat(result.queryContext()).isNotNull();
        }

        @Test
        @DisplayName("상태 필터가 있으면 Criteria에 반영한다")
        void createCriteria_WithStatusFilter_ReflectsStatus() {
            // given
            long productGroupId = 100L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(productGroupId, "COMPLETED");

            // when
            SyncHistorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statusFilter()).isEqualTo(SyncStatus.COMPLETED);
            assertThat(result.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태 필터를 변환한다")
        void createCriteria_WithFailedStatus_ReflectsFailedStatus() {
            // given
            long productGroupId = 100L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(productGroupId, "FAILED");

            // when
            SyncHistorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statusFilter()).isEqualTo(SyncStatus.FAILED);
        }

        @Test
        @DisplayName("PENDING 상태 필터를 변환한다")
        void createCriteria_WithPendingStatus_ReflectsPendingStatus() {
            // given
            long productGroupId = 100L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(productGroupId, "PENDING");

            // when
            SyncHistorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statusFilter()).isEqualTo(SyncStatus.PENDING);
        }

        @Test
        @DisplayName("유효하지 않은 상태 문자열은 null로 처리된다")
        void createCriteria_WithInvalidStatus_ReturnsNullStatusFilter() {
            // given
            long productGroupId = 100L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(
                            productGroupId, "UNKNOWN_STATUS");

            // when
            SyncHistorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statusFilter()).isNull();
            assertThat(result.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("상태가 null이면 statusFilter가 null이다")
        void createCriteria_NullStatus_ReturnsNullStatusFilter() {
            // given
            long productGroupId = 100L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(productGroupId, (String) null);

            // when
            SyncHistorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statusFilter()).isNull();
            assertThat(result.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("상태가 빈 문자열이면 statusFilter가 null이다")
        void createCriteria_BlankStatus_ReturnsNullStatusFilter() {
            // given
            long productGroupId = 100L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(productGroupId, "  ");

            // when
            SyncHistorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.statusFilter()).isNull();
        }

        @Test
        @DisplayName("페이징 컨텍스트가 올바르게 생성된다")
        void createCriteria_WithPagination_CreatesCorrectQueryContext() {
            // given
            long productGroupId = 100L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(productGroupId, 1, 10);

            // when
            SyncHistorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.page()).isEqualTo(1);
            assertThat(result.size()).isEqualTo(10);
        }
    }
}
