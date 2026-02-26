package com.ryuqq.marketplace.application.brandpreset.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brandpreset.BrandPresetQueryFixtures;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import com.ryuqq.marketplace.application.brandpreset.port.out.query.BrandPresetQueryPort;
import com.ryuqq.marketplace.domain.brandpreset.BrandPresetFixtures;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetNotFoundException;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("BrandPresetReadManager 단위 테스트")
class BrandPresetReadManagerTest {

    @InjectMocks private BrandPresetReadManager sut;

    @Mock private BrandPresetQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 브랜드 프리셋을 반환한다")
        void getById_ExistingId_ReturnsBrandPreset() {
            // given
            BrandPresetId id = BrandPresetId.of(1L);
            BrandPreset expected = BrandPresetFixtures.activeBrandPreset(1L);

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            BrandPreset result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NonExistingId_ThrowsException() {
            // given
            BrandPresetId id = BrandPresetId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(BrandPresetNotFoundException.class);
            then(queryPort).should().findById(id);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건에 맞는 브랜드 프리셋 목록을 반환한다")
        void findByCriteria_ValidCriteria_ReturnsResults() {
            // given
            BrandPresetSearchCriteria criteria = BrandPresetFixtures.defaultSearchCriteria();
            List<BrandPresetResult> expected =
                    List.of(
                            BrandPresetQueryFixtures.brandPresetResult(1L),
                            BrandPresetQueryFixtures.brandPresetResult(2L));

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<BrandPresetResult> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result).hasSize(2);
            then(queryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void findByCriteria_NoResults_ReturnsEmptyList() {
            // given
            BrandPresetSearchCriteria criteria = BrandPresetFixtures.defaultSearchCriteria();
            List<BrandPresetResult> emptyList = List.of();

            given(queryPort.findByCriteria(criteria)).willReturn(emptyList);

            // when
            List<BrandPresetResult> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 개수 조회")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건에 맞는 브랜드 프리셋 개수를 반환한다")
        void countByCriteria_ValidCriteria_ReturnsCount() {
            // given
            BrandPresetSearchCriteria criteria = BrandPresetFixtures.defaultSearchCriteria();
            long expectedCount = 10L;

            given(queryPort.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(queryPort).should().countByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("findAllByIds() - ID 목록으로 조회")
    class FindAllByIdsTest {

        @Test
        @DisplayName("여러 ID로 브랜드 프리셋 목록을 조회한다")
        void findAllByIds_ValidIds_ReturnsBrandPresets() {
            // given
            List<Long> ids = List.of(1L, 2L, 3L);
            List<BrandPreset> expected =
                    List.of(
                            BrandPresetFixtures.activeBrandPreset(1L),
                            BrandPresetFixtures.activeBrandPreset(2L),
                            BrandPresetFixtures.activeBrandPreset(3L));

            given(queryPort.findAllByIds(ids)).willReturn(expected);

            // when
            List<BrandPreset> result = sut.findAllByIds(ids);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result).hasSize(3);
            then(queryPort).should().findAllByIds(ids);
        }
    }

    @Nested
    @DisplayName("findSalesChannelIdBySalesChannelBrandId() - 판매채널 ID 조회")
    class FindSalesChannelIdBySalesChannelBrandIdTest {

        @Test
        @DisplayName("판매채널 브랜드 ID로 판매채널 ID를 조회한다")
        void findSalesChannelIdBySalesChannelBrandId_ValidId_ReturnsSalesChannelId() {
            // given
            Long salesChannelBrandId = 100L;
            Long expectedSalesChannelId = 1L;

            given(queryPort.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId))
                    .willReturn(Optional.of(expectedSalesChannelId));

            // when
            Optional<Long> result =
                    sut.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expectedSalesChannelId);
            then(queryPort).should().findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId);
        }

        @Test
        @DisplayName("존재하지 않는 판매채널 브랜드 ID면 Optional.empty를 반환한다")
        void findSalesChannelIdBySalesChannelBrandId_NonExisting_ReturnsEmpty() {
            // given
            Long salesChannelBrandId = 999L;

            given(queryPort.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId))
                    .willReturn(Optional.empty());

            // when
            Optional<Long> result =
                    sut.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId);
        }
    }
}
