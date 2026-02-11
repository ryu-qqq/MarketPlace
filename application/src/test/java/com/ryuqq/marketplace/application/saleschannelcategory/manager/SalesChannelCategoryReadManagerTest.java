package com.ryuqq.marketplace.application.saleschannelcategory.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.saleschannelcategory.port.out.query.SalesChannelCategoryQueryPort;
import com.ryuqq.marketplace.domain.saleschannelcategory.SalesChannelCategoryFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.exception.SalesChannelCategoryNotFoundException;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
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
@DisplayName("SalesChannelCategoryReadManager 단위 테스트")
class SalesChannelCategoryReadManagerTest {

    @InjectMocks private SalesChannelCategoryReadManager sut;

    @Mock private SalesChannelCategoryQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 SalesChannelCategory를 조회한다")
        void getById_Exists_ReturnsCategory() {
            // given
            SalesChannelCategoryId id = SalesChannelCategoryId.of(1L);
            SalesChannelCategory expected =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            SalesChannelCategory result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            SalesChannelCategoryId id = SalesChannelCategoryId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(SalesChannelCategoryNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 SalesChannelCategory 목록을 조회한다")
        void findByCriteria_ReturnsCategories() {
            // given
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.saleschannelcategory.query
                                            .SalesChannelCategorySortKey.defaultKey()));
            List<SalesChannelCategory> expected =
                    List.of(SalesChannelCategoryFixtures.activeSalesChannelCategory());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<SalesChannelCategory> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 카테고리가 없으면 빈 목록을 반환한다")
        void findByCriteria_NoMatches_ReturnsEmptyList() {
            // given
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.saleschannelcategory.query
                                            .SalesChannelCategorySortKey.defaultKey()));
            List<SalesChannelCategory> expected = List.of();

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<SalesChannelCategory> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 SalesChannelCategory 수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.saleschannelcategory.query
                                            .SalesChannelCategorySortKey.defaultKey()));
            long expected = 10L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 카테고리가 없으면 0을 반환한다")
        void countByCriteria_NoMatches_ReturnsZero() {
            // given
            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.saleschannelcategory.query
                                            .SalesChannelCategorySortKey.defaultKey()));
            long expected = 0L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("existsBySalesChannelIdAndExternalCode() - 외부 코드 존재 여부 확인")
    class ExistsBySalesChannelIdAndExternalCodeTest {

        @Test
        @DisplayName("판매채널과 외부 코드로 존재 여부를 확인한다")
        void existsBySalesChannelIdAndExternalCode_Exists_ReturnsTrue() {
            // given
            Long salesChannelId = 1L;
            String externalCategoryCode = "CAT001";

            given(
                            queryPort.existsBySalesChannelIdAndExternalCode(
                                    salesChannelId, externalCategoryCode))
                    .willReturn(true);

            // when
            boolean result =
                    sut.existsBySalesChannelIdAndExternalCode(salesChannelId, externalCategoryCode);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않으면 false를 반환한다")
        void existsBySalesChannelIdAndExternalCode_NotExists_ReturnsFalse() {
            // given
            Long salesChannelId = 1L;
            String externalCategoryCode = "CAT999";

            given(
                            queryPort.existsBySalesChannelIdAndExternalCode(
                                    salesChannelId, externalCategoryCode))
                    .willReturn(false);

            // when
            boolean result =
                    sut.existsBySalesChannelIdAndExternalCode(salesChannelId, externalCategoryCode);

            // then
            assertThat(result).isFalse();
        }
    }
}
