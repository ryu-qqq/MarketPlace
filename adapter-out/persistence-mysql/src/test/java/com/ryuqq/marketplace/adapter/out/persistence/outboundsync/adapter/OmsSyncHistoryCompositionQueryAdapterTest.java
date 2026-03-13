package com.ryuqq.marketplace.adapter.out.persistence.outboundsync.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.SyncHistoryCompositeDtoFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.composite.SyncHistoryCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.mapper.OmsSyncHistoryCompositionMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OmsSyncHistoryCompositionQueryDslRepository;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySearchCriteria;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySortKey;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * OmsSyncHistoryCompositionQueryAdapterTest - 연동 이력 Composition 조회 어댑터 단위 테스트.
 *
 * <p>1-pass 전략: 단일 JOIN 쿼리로 조회 후 매핑하는 로직 검증.
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OmsSyncHistoryCompositionQueryAdapter 단위 테스트")
class OmsSyncHistoryCompositionQueryAdapterTest {

    @Mock private OmsSyncHistoryCompositionQueryDslRepository compositionRepository;

    @Mock private OmsSyncHistoryCompositionMapper mapper;

    @InjectMocks private OmsSyncHistoryCompositionQueryAdapter adapter;

    private SyncHistorySearchCriteria defaultCriteria() {
        QueryContext<SyncHistorySortKey> queryContext =
                QueryContext.of(
                        SyncHistorySortKey.CREATED_AT, SortDirection.DESC, PageRequest.of(0, 20));
        return new SyncHistorySearchCriteria(100L, null, null, queryContext);
    }

    // ========================================================================
    // 1. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("composites가 있으면 매핑하여 반환합니다")
        void findByCriteria_WithComposites_MapsAndReturnsResults() {
            // given
            SyncHistorySearchCriteria criteria = defaultCriteria();
            List<SyncHistoryCompositeDto> composites =
                    List.of(SyncHistoryCompositeDtoFixtures.completedDto(1L));
            List<SyncHistoryListResult> expected =
                    List.of(
                            new SyncHistoryListResult(
                                    1L,
                                    "테스트 외부몰",
                                    "test-account-001",
                                    "디폴트 프리셋",
                                    "COMPLETED",
                                    "연동완료",
                                    0,
                                    null,
                                    "EXT-PROD-001",
                                    Instant.now().minusSeconds(7200),
                                    Instant.now().minusSeconds(3600)));

            given(compositionRepository.findByCriteria(criteria)).willReturn(composites);
            given(mapper.toResults(composites)).willReturn(expected);

            // when
            List<SyncHistoryListResult> results = adapter.findByCriteria(criteria);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).id()).isEqualTo(1L);
            assertThat(results.get(0).shopName()).isEqualTo("테스트 외부몰");
            then(compositionRepository).should().findByCriteria(criteria);
            then(mapper).should().toResults(composites);
        }

        @Test
        @DisplayName("composites가 비어있으면 mapper를 호출하지 않고 빈 목록을 반환합니다")
        void findByCriteria_WithEmptyComposites_ReturnsEmptyWithoutMapper() {
            // given
            SyncHistorySearchCriteria criteria = defaultCriteria();
            given(compositionRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<SyncHistoryListResult> results = adapter.findByCriteria(criteria);

            // then
            assertThat(results).isEmpty();
            then(compositionRepository).should().findByCriteria(criteria);
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("여러 composites가 있으면 전체를 mapper에 전달합니다")
        void findByCriteria_WithMultipleComposites_PassesAllToMapper() {
            // given
            SyncHistorySearchCriteria criteria = defaultCriteria();
            List<SyncHistoryCompositeDto> composites =
                    SyncHistoryCompositeDtoFixtures.completedDtoList(3);

            given(compositionRepository.findByCriteria(criteria)).willReturn(composites);
            given(mapper.toResults(composites)).willReturn(List.of());

            // when
            adapter.findByCriteria(criteria);

            // then
            then(mapper).should().toResults(composites);
        }
    }

    // ========================================================================
    // 2. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("compositionRepository의 countByCriteria를 그대로 위임합니다")
        void countByCriteria_DelegatesToRepository() {
            // given
            SyncHistorySearchCriteria criteria = defaultCriteria();
            given(compositionRepository.countByCriteria(criteria)).willReturn(15L);

            // when
            long count = adapter.countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(15L);
            then(compositionRepository).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("데이터가 없으면 0을 반환합니다")
        void countByCriteria_WithNoData_ReturnsZero() {
            // given
            SyncHistorySearchCriteria criteria = defaultCriteria();
            given(compositionRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long count = adapter.countByCriteria(criteria);

            // then
            assertThat(count).isZero();
        }
    }
}
