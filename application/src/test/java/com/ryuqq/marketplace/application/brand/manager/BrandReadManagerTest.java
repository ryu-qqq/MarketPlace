package com.ryuqq.marketplace.application.brand.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brand.port.out.query.BrandQueryPort;
import com.ryuqq.marketplace.domain.brand.BrandFixtures;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brand.exception.BrandNotFoundException;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria;
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
@DisplayName("BrandReadManager 단위 테스트")
class BrandReadManagerTest {

    @InjectMocks private BrandReadManager sut;

    @Mock private BrandQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 브랜드를 ID로 조회한다")
        void getById_ExistsId_ReturnsBrand() {
            // given
            BrandId id = BrandId.of(1L);
            Brand expected = BrandFixtures.activeBrand(1L);

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Brand result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
        void getById_NotExistsId_ThrowsException() {
            // given
            BrandId id = BrandId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(BrandNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 브랜드 리스트를 조회한다")
        void findByCriteria_ValidCriteria_ReturnsList() {
            // given
            BrandSearchCriteria criteria = null; // mock 대상
            List<Brand> expected =
                    List.of(
                            BrandFixtures.activeBrand(1L),
                            BrandFixtures.activeBrand(2L));

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<Brand> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 브랜드가 없으면 빈 리스트를 반환한다")
        void findByCriteria_NoMatches_ReturnsEmptyList() {
            // given
            BrandSearchCriteria criteria = null;
            List<Brand> emptyList = List.of();

            given(queryPort.findByCriteria(criteria)).willReturn(emptyList);

            // when
            List<Brand> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 브랜드 개수를 조회한다")
        void countByCriteria_ValidCriteria_ReturnsCount() {
            // given
            BrandSearchCriteria criteria = null;
            long expectedCount = 5L;

            given(queryPort.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(queryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("조건에 맞는 브랜드가 없으면 0을 반환한다")
        void countByCriteria_NoMatches_ReturnsZero() {
            // given
            BrandSearchCriteria criteria = null;

            given(queryPort.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    @Nested
    @DisplayName("existsByCode() - 브랜드 코드 중복 체크")
    class ExistsByCodeTest {

        @Test
        @DisplayName("이미 존재하는 브랜드 코드면 true를 반환한다")
        void existsByCode_Exists_ReturnsTrue() {
            // given
            String code = "TEST_BRAND";

            given(queryPort.existsByCode(code)).willReturn(true);

            // when
            boolean result = sut.existsByCode(code);

            // then
            assertThat(result).isTrue();
            then(queryPort).should().existsByCode(code);
        }

        @Test
        @DisplayName("존재하지 않는 브랜드 코드면 false를 반환한다")
        void existsByCode_NotExists_ReturnsFalse() {
            // given
            String code = "NEW_BRAND";

            given(queryPort.existsByCode(code)).willReturn(false);

            // when
            boolean result = sut.existsByCode(code);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findAllByIds() - 여러 ID로 조회")
    class FindAllByIdsTest {

        @Test
        @DisplayName("여러 ID로 브랜드 리스트를 조회한다")
        void findAllByIds_ValidIds_ReturnsList() {
            // given
            List<Long> ids = List.of(1L, 2L, 3L);
            List<Brand> expected =
                    List.of(
                            BrandFixtures.activeBrand(1L),
                            BrandFixtures.activeBrand(2L),
                            BrandFixtures.activeBrand(3L));

            given(queryPort.findAllByIds(ids)).willReturn(expected);

            // when
            List<Brand> result = sut.findAllByIds(ids);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findAllByIds(ids);
        }

        @Test
        @DisplayName("빈 ID 리스트로 조회하면 빈 리스트를 반환한다")
        void findAllByIds_EmptyIds_ReturnsEmptyList() {
            // given
            List<Long> emptyIds = List.of();
            List<Brand> emptyList = List.of();

            given(queryPort.findAllByIds(emptyIds)).willReturn(emptyList);

            // when
            List<Brand> result = sut.findAllByIds(emptyIds);

            // then
            assertThat(result).isEmpty();
        }
    }
}
