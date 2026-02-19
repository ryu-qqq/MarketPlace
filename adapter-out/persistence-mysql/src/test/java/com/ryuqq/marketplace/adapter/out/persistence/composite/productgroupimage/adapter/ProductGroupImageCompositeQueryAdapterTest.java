package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;

import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto.ProductGroupImageCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto.ProductGroupImageCompositeDtoFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.mapper.ProductGroupImageCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.repository.ProductGroupImageCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
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
 * ProductGroupImageCompositeQueryAdapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>이미지 Composite 조회 Adapter가 Repository와 Mapper를 올바르게 조합하는지 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupImageCompositeQueryAdapter 단위 테스트")
class ProductGroupImageCompositeQueryAdapterTest {

    @Mock private ProductGroupImageCompositeQueryDslRepository compositeRepository;

    @Mock private ProductGroupImageCompositeMapper compositeMapper;

    @InjectMocks private ProductGroupImageCompositeQueryAdapter queryAdapter;

    @Nested
    @DisplayName("findImageUploadStatus 메서드 테스트")
    class FindImageUploadStatusTest {

        @Test
        @DisplayName("productGroupId로 조회 시 이미지 업로드 상태 결과를 반환합니다")
        void findImageUploadStatus_WithExistingProductGroupId_ReturnsResult() {
            // given
            Long productGroupId = 1L;
            ProductGroupImageCompositeDto dto =
                    ProductGroupImageCompositeDtoFixtures.defaultCompositeDto(productGroupId);

            ProductGroupImageUploadStatusResult expected =
                    new ProductGroupImageUploadStatusResult(
                            productGroupId, 2, 2, 0, 0, 0, List.of());

            given(compositeRepository.findByProductGroupId(productGroupId)).willReturn(dto);
            given(compositeMapper.toResult(dto)).willReturn(expected);

            // when
            ProductGroupImageUploadStatusResult result =
                    queryAdapter.findImageUploadStatus(productGroupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productGroupId()).isEqualTo(productGroupId);
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.completedCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("Repository와 Mapper가 각각 정확히 한 번만 호출됩니다")
        void findImageUploadStatus_CallsRepositoryAndMapperExactlyOnce() {
            // given
            Long productGroupId = 2L;
            ProductGroupImageCompositeDto dto =
                    ProductGroupImageCompositeDtoFixtures.emptyCompositeDto(productGroupId);
            ProductGroupImageUploadStatusResult expected =
                    new ProductGroupImageUploadStatusResult(
                            productGroupId, 0, 0, 0, 0, 0, List.of());

            given(compositeRepository.findByProductGroupId(productGroupId)).willReturn(dto);
            given(compositeMapper.toResult(dto)).willReturn(expected);

            // when
            queryAdapter.findImageUploadStatus(productGroupId);

            // then
            then(compositeRepository).should(only()).findByProductGroupId(productGroupId);
            then(compositeMapper).should(only()).toResult(dto);
        }

        @Test
        @DisplayName("이미지가 없는 경우 totalCount가 0인 결과를 반환합니다")
        void findImageUploadStatus_WithEmptyImages_ReturnsTotalCountZero() {
            // given
            Long productGroupId = 3L;
            ProductGroupImageCompositeDto dto =
                    ProductGroupImageCompositeDtoFixtures.emptyCompositeDto(productGroupId);
            ProductGroupImageUploadStatusResult expected =
                    new ProductGroupImageUploadStatusResult(
                            productGroupId, 0, 0, 0, 0, 0, List.of());

            given(compositeRepository.findByProductGroupId(productGroupId)).willReturn(dto);
            given(compositeMapper.toResult(dto)).willReturn(expected);

            // when
            ProductGroupImageUploadStatusResult result =
                    queryAdapter.findImageUploadStatus(productGroupId);

            // then
            assertThat(result.totalCount()).isEqualTo(0);
            assertThat(result.images()).isEmpty();
        }

        @Test
        @DisplayName("혼합 상태의 이미지가 있을 때 Mapper 결과를 그대로 반환합니다")
        void findImageUploadStatus_WithMixedStatus_ReturnsMappedResult() {
            // given
            Long productGroupId = 4L;
            ProductGroupImageCompositeDto dto =
                    ProductGroupImageCompositeDtoFixtures.mixedStatusCompositeDto(productGroupId);

            ProductGroupImageUploadStatusResult expected =
                    new ProductGroupImageUploadStatusResult(
                            productGroupId, 3, 1, 1, 0, 1, List.of());

            given(compositeRepository.findByProductGroupId(productGroupId)).willReturn(dto);
            given(compositeMapper.toResult(dto)).willReturn(expected);

            // when
            ProductGroupImageUploadStatusResult result =
                    queryAdapter.findImageUploadStatus(productGroupId);

            // then
            assertThat(result.totalCount()).isEqualTo(3);
            assertThat(result.completedCount()).isEqualTo(1);
            assertThat(result.pendingCount()).isEqualTo(1);
            assertThat(result.failedCount()).isEqualTo(1);
            assertThat(result.processingCount()).isEqualTo(0);
        }
    }
}
