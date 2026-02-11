package com.ryuqq.marketplace.application.categorypreset.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.categorypreset.CategoryPresetQueryFixtures;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetResult;
import com.ryuqq.marketplace.application.categorypreset.port.out.query.CategoryPresetQueryPort;
import com.ryuqq.marketplace.domain.categorypreset.CategoryPresetFixtures;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetNotFoundException;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.categorypreset.query.CategoryPresetSearchCriteria;
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
@DisplayName("CategoryPresetReadManager 단위 테스트")
class CategoryPresetReadManagerTest {

    @InjectMocks private CategoryPresetReadManager sut;

    @Mock private CategoryPresetQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 CategoryPreset을 조회한다")
        void getById_Exists_ReturnsCategoryPreset() {
            // given
            CategoryPresetId id = CategoryPresetId.of(1L);
            CategoryPreset expected = CategoryPresetFixtures.activeCategoryPreset();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            CategoryPreset result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            CategoryPresetId id = CategoryPresetId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(CategoryPresetNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 CategoryPreset 목록을 조회한다")
        void findByCriteria_ReturnsCategoryPresets() {
            // given
            CategoryPresetSearchCriteria criteria = CategoryPresetFixtures.defaultSearchCriteria();
            List<CategoryPresetResult> expected =
                    List.of(
                            CategoryPresetQueryFixtures.categoryPresetResult(1L),
                            CategoryPresetQueryFixtures.categoryPresetResult(2L));

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<CategoryPresetResult> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            then(queryPort).should().findByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 CategoryPreset 수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            CategoryPresetSearchCriteria criteria = CategoryPresetFixtures.defaultSearchCriteria();
            long expected = 10L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("findAllByIds() - ID 목록으로 조회")
    class FindAllByIdsTest {

        @Test
        @DisplayName("ID 목록으로 CategoryPreset들을 조회한다")
        void findAllByIds_ReturnsCategoryPresets() {
            // given
            List<Long> ids = List.of(1L, 2L, 3L);
            List<CategoryPreset> expected =
                    List.of(
                            CategoryPresetFixtures.activeCategoryPreset(1L),
                            CategoryPresetFixtures.activeCategoryPreset(2L),
                            CategoryPresetFixtures.activeCategoryPreset(3L));

            given(queryPort.findAllByIds(ids)).willReturn(expected);

            // when
            List<CategoryPreset> result = sut.findAllByIds(ids);

            // then
            assertThat(result).hasSize(3);
            then(queryPort).should().findAllByIds(ids);
        }
    }

    @Nested
    @DisplayName("findSalesChannelCategoryIdByCode() - 판매채널 카테고리 ID 조회")
    class FindSalesChannelCategoryIdByCodeTest {

        @Test
        @DisplayName("카테고리 코드로 판매채널 카테고리 ID를 조회한다")
        void findSalesChannelCategoryIdByCode_ReturnsId() {
            // given
            Long salesChannelId = 1L;
            String categoryCode = "TEST_CODE";
            Long expected = 200L;

            given(queryPort.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode))
                    .willReturn(Optional.of(expected));

            // when
            Optional<Long> result = sut.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 코드면 빈 Optional을 반환한다")
        void findSalesChannelCategoryIdByCode_NotExists_ReturnsEmpty() {
            // given
            Long salesChannelId = 1L;
            String categoryCode = "INVALID_CODE";

            given(queryPort.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode))
                    .willReturn(Optional.empty());

            // when
            Optional<Long> result = sut.findSalesChannelCategoryIdByCode(salesChannelId, categoryCode);

            // then
            assertThat(result).isEmpty();
        }
    }
}
