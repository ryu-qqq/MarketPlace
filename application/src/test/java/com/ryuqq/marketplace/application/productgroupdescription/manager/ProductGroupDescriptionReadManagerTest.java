package com.ryuqq.marketplace.application.productgroupdescription.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupdescription.port.out.query.ProductGroupDescriptionQueryPort;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupDescriptionNotFoundException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
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
@DisplayName("ProductGroupDescriptionReadManager 단위 테스트")
class ProductGroupDescriptionReadManagerTest {

    @InjectMocks private ProductGroupDescriptionReadManager sut;

    @Mock private ProductGroupDescriptionQueryPort queryPort;

    @Nested
    @DisplayName("getByProductGroupId() - ProductGroupId로 상세설명 조회")
    class GetByProductGroupIdTest {

        @Test
        @DisplayName("존재하는 ProductGroupId로 조회하면 Description을 반환한다")
        void getByProductGroupId_Exists_ReturnsDescription() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            given(queryPort.findByProductGroupId(productGroupId))
                    .willReturn(Optional.of(description));

            // when
            ProductGroupDescription result = sut.getByProductGroupId(productGroupId);

            // then
            assertThat(result).isEqualTo(description);
        }

        @Test
        @DisplayName("존재하지 않는 ProductGroupId로 조회하면 예외가 발생한다")
        void getByProductGroupId_NotExists_ThrowsException() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            given(queryPort.findByProductGroupId(productGroupId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getByProductGroupId(productGroupId))
                    .isInstanceOf(ProductGroupDescriptionNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByProductGroupId() - Optional로 조회")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("존재하면 Optional.of(description)를 반환한다")
        void findByProductGroupId_Exists_ReturnsOptionalWithDescription() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();
            given(queryPort.findByProductGroupId(productGroupId))
                    .willReturn(Optional.of(description));

            // when
            Optional<ProductGroupDescription> result = sut.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
        }

        @Test
        @DisplayName("존재하지 않으면 Optional.empty()를 반환한다")
        void findByProductGroupId_NotExists_ReturnsOptionalEmpty() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            given(queryPort.findByProductGroupId(productGroupId)).willReturn(Optional.empty());

            // when
            Optional<ProductGroupDescription> result = sut.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findPublishReady() - PUBLISH_READY 상태 조회")
    class FindPublishReadyTest {

        @Test
        @DisplayName("PUBLISH_READY 상태의 Description 목록을 반환한다")
        void findPublishReady_ReturnsPublishReadyDescriptions() {
            // given
            int limit = 10;
            List<ProductGroupDescription> descriptions =
                    List.of(ProductGroupFixtures.defaultProductGroupDescription());
            given(queryPort.findByPublishStatus(DescriptionPublishStatus.PUBLISH_READY, limit))
                    .willReturn(descriptions);

            // when
            List<ProductGroupDescription> result = sut.findPublishReady(limit);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("PUBLISH_READY 상태의 Description이 없으면 빈 목록을 반환한다")
        void findPublishReady_NoneExists_ReturnsEmptyList() {
            // given
            int limit = 10;
            given(queryPort.findByPublishStatus(DescriptionPublishStatus.PUBLISH_READY, limit))
                    .willReturn(List.of());

            // when
            List<ProductGroupDescription> result = sut.findPublishReady(limit);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByProductGroupIds() - 여러 ProductGroupId로 상세설명 배치 조회")
    class FindByProductGroupIdsTest {

        @Test
        @DisplayName("여러 ProductGroupId로 상세설명 목록을 배치 조회한다")
        void findByProductGroupIds_ValidIds_ReturnsDescriptions() {
            // given
            List<ProductGroupId> productGroupIds =
                    List.of(ProductGroupFixtures.defaultProductGroupId(), ProductGroupId.of(2L));
            List<ProductGroupDescription> expected =
                    List.of(ProductGroupFixtures.defaultProductGroupDescription());

            given(queryPort.findByProductGroupIdIn(productGroupIds)).willReturn(expected);

            // when
            List<ProductGroupDescription> result = sut.findByProductGroupIds(productGroupIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("빈 productIds 목록이면 쿼리를 실행하지 않고 빈 목록을 반환한다")
        void findByProductGroupIds_EmptyIds_ReturnsEmptyWithoutQuery() {
            // when
            List<ProductGroupDescription> result = sut.findByProductGroupIds(List.of());

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
            List<ProductGroupDescription> result = sut.findByProductGroupIds(productGroupIds);

            // then
            assertThat(result).isEmpty();
        }
    }
}
