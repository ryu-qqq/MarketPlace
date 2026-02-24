package com.ryuqq.marketplace.application.productnotice.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productnotice.port.out.query.ProductNoticeQueryPort;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.exception.ProductNoticeNotFoundException;
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
@DisplayName("ProductNoticeReadManager 단위 테스트")
class ProductNoticeReadManagerTest {

    @InjectMocks private ProductNoticeReadManager sut;

    @Mock private ProductNoticeQueryPort queryPort;

    @Nested
    @DisplayName("findByProductGroupId() - Optional 조회")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("존재하는 고시정보를 Optional로 반환한다")
        void findByProductGroupId_ExistingNotice_ReturnsOptional() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();

            given(queryPort.findByProductGroupId(productGroupId)).willReturn(Optional.of(notice));

            // when
            Optional<ProductNotice> result = sut.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(notice);
        }

        @Test
        @DisplayName("존재하지 않는 ProductGroup의 고시정보는 빈 Optional을 반환한다")
        void findByProductGroupId_NonExistingNotice_ReturnsEmpty() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            given(queryPort.findByProductGroupId(productGroupId)).willReturn(Optional.empty());

            // when
            Optional<ProductNotice> result = sut.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getByProductGroupId() - 필수 조회")
    class GetByProductGroupIdTest {

        @Test
        @DisplayName("존재하는 고시정보를 반환한다")
        void getByProductGroupId_ExistingNotice_ReturnsNotice() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            ProductNotice notice = ProductNoticeFixtures.existingProductNotice();

            given(queryPort.findByProductGroupId(productGroupId)).willReturn(Optional.of(notice));

            // when
            ProductNotice result = sut.getByProductGroupId(productGroupId);

            // then
            assertThat(result).isEqualTo(notice);
        }

        @Test
        @DisplayName("존재하지 않으면 예외를 던진다")
        void getByProductGroupId_NonExistingNotice_ThrowsException() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            given(queryPort.findByProductGroupId(productGroupId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getByProductGroupId(productGroupId))
                    .isInstanceOf(ProductNoticeNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByProductGroupIds() - 여러 ProductGroupId로 고시정보 배치 조회")
    class FindByProductGroupIdsTest {

        @Test
        @DisplayName("여러 ProductGroupId로 고시정보 목록을 배치 조회한다")
        void findByProductGroupIds_ValidIds_ReturnsNotices() {
            // given
            List<ProductGroupId> productGroupIds =
                    List.of(ProductGroupFixtures.defaultProductGroupId(), ProductGroupId.of(2L));
            List<ProductNotice> expected =
                    List.of(
                            ProductNoticeFixtures.existingProductNotice(1L),
                            ProductNoticeFixtures.existingProductNotice(2L));

            given(queryPort.findByProductGroupIdIn(productGroupIds)).willReturn(expected);

            // when
            List<ProductNotice> result = sut.findByProductGroupIds(productGroupIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findByProductGroupIdIn(productGroupIds);
        }

        @Test
        @DisplayName("빈 productGroupIds 목록이면 쿼리를 실행하지 않고 빈 목록을 반환한다")
        void findByProductGroupIds_EmptyIds_ReturnsEmptyWithoutQuery() {
            // when
            List<ProductNotice> result = sut.findByProductGroupIds(List.of());

            // then
            assertThat(result).isEmpty();
            then(queryPort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("결과가 없으면 빈 목록을 반환한다")
        void findByProductGroupIds_NoResults_ReturnsEmptyList() {
            // given
            List<ProductGroupId> productGroupIds =
                    List.of(ProductGroupFixtures.defaultProductGroupId());

            given(queryPort.findByProductGroupIdIn(productGroupIds)).willReturn(List.of());

            // when
            List<ProductNotice> result = sut.findByProductGroupIds(productGroupIds);

            // then
            assertThat(result).isEmpty();
        }
    }
}
