package com.ryuqq.marketplace.application.productgroupimage.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.productgroupimage.port.out.query.ProductGroupImageQueryPort;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.exception.ProductGroupImageNotFoundException;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
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
@DisplayName("ProductGroupImageReadManager 단위 테스트")
class ProductGroupImageReadManagerTest {

    @InjectMocks private ProductGroupImageReadManager sut;

    @Mock private ProductGroupImageQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 이미지 단건 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 이미지 ID로 조회하면 이미지를 반환한다")
        void getById_ExistingId_ReturnsImage() {
            // given
            Long imageId = 1L;
            ProductGroupImage expectedImage = ProductGroupFixtures.thumbnailImage();

            given(queryPort.findById(imageId)).willReturn(Optional.of(expectedImage));

            // when
            ProductGroupImage result = sut.getById(imageId);

            // then
            assertThat(result).isEqualTo(expectedImage);
        }

        @Test
        @DisplayName("존재하지 않는 이미지 ID로 조회하면 예외를 던진다")
        void getById_NonExistingId_ThrowsException() {
            // given
            Long imageId = 999L;
            given(queryPort.findById(imageId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(imageId))
                    .isInstanceOf(ProductGroupImageNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getByProductGroupId() - ProductGroupId로 이미지 목록 조회")
    class GetByProductGroupIdTest {

        @Test
        @DisplayName("ProductGroupId로 이미지 목록을 조회하고 ProductGroupImages를 반환한다")
        void getByProductGroupId_ExistingProductGroup_ReturnsImages() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            ProductGroupImage image1 = ProductGroupFixtures.thumbnailImage();
            ProductGroupImage image2 = ProductGroupFixtures.detailImage(1);

            given(queryPort.findByProductGroupId(productGroupId))
                    .willReturn(List.of(image1, image2));

            // when
            ProductGroupImages result = sut.getByProductGroupId(productGroupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toList()).hasSize(2);
        }

        @Test
        @DisplayName("이미지가 없는 ProductGroup은 빈 ProductGroupImages를 반환한다")
        void getByProductGroupId_NoImages_ReturnsEmptyImages() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            given(queryPort.findByProductGroupId(productGroupId)).willReturn(List.of());

            // when
            ProductGroupImages result = sut.getByProductGroupId(productGroupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toList()).isEmpty();
        }
    }
}
