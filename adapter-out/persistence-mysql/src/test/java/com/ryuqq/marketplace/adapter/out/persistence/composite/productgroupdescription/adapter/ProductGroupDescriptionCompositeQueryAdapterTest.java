package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;

import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.DescriptionCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.ProductGroupDescriptionCompositeDtoFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.mapper.ProductGroupDescriptionCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.repository.ProductGroupDescriptionCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
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

/**
 * ProductGroupDescriptionCompositeQueryAdapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>상세설명 Composite 조회 Adapter가 Repository와 Mapper를 올바르게 조합하는지 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupDescriptionCompositeQueryAdapter 단위 테스트")
class ProductGroupDescriptionCompositeQueryAdapterTest {

    @Mock private ProductGroupDescriptionCompositeQueryDslRepository compositeRepository;

    @Mock private ProductGroupDescriptionCompositeMapper compositeMapper;

    @InjectMocks private ProductGroupDescriptionCompositeQueryAdapter queryAdapter;

    @Nested
    @DisplayName("findPublishStatus 메서드 테스트")
    class FindPublishStatusTest {

        @Test
        @DisplayName("상세설명이 존재할 때 Composite DTO를 Mapper로 변환한 결과를 반환합니다")
        void findPublishStatus_WithExistingDescription_ReturnsResult() {
            // given
            Long productGroupId = 1L;
            DescriptionCompositeDto dto =
                    ProductGroupDescriptionCompositeDtoFixtures.defaultCompositeDto(productGroupId);

            DescriptionPublishStatusResult expected =
                    new DescriptionPublishStatusResult(
                            productGroupId,
                            ProductGroupDescriptionCompositeDtoFixtures.DEFAULT_DESCRIPTION_ID,
                            "PENDING",
                            null,
                            2,
                            2,
                            0,
                            0,
                            List.of());

            given(compositeRepository.findByProductGroupId(productGroupId))
                    .willReturn(Optional.of(dto));
            given(compositeMapper.toResult(dto)).willReturn(expected);

            // when
            DescriptionPublishStatusResult result = queryAdapter.findPublishStatus(productGroupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productGroupId()).isEqualTo(productGroupId);
            assertThat(result.descriptionId())
                    .isEqualTo(ProductGroupDescriptionCompositeDtoFixtures.DEFAULT_DESCRIPTION_ID);
            assertThat(result.publishStatus()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("상세설명이 존재하지 않을 때 empty 결과를 반환합니다")
        void findPublishStatus_WithNonExistingDescription_ReturnsEmpty() {
            // given
            Long productGroupId = 999L;
            given(compositeRepository.findByProductGroupId(productGroupId))
                    .willReturn(Optional.empty());

            // when
            DescriptionPublishStatusResult result = queryAdapter.findPublishStatus(productGroupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productGroupId()).isEqualTo(productGroupId);
            assertThat(result.descriptionId()).isNull();
            assertThat(result.publishStatus()).isNull();
            assertThat(result.totalImageCount()).isEqualTo(0);
            assertThat(result.images()).isEmpty();
        }

        @Test
        @DisplayName("상세설명이 없을 때 Mapper가 호출되지 않습니다")
        void findPublishStatus_WithNonExistingDescription_MapperNotCalled() {
            // given
            Long productGroupId = 998L;
            given(compositeRepository.findByProductGroupId(productGroupId))
                    .willReturn(Optional.empty());

            // when
            queryAdapter.findPublishStatus(productGroupId);

            // then
            then(compositeMapper).should(never()).toResult(org.mockito.ArgumentMatchers.any());
        }

        @Test
        @DisplayName("Repository와 Mapper가 각각 정확히 한 번씩 호출됩니다")
        void findPublishStatus_WithExistingDescription_CallsRepositoryAndMapperOnce() {
            // given
            Long productGroupId = 2L;
            DescriptionCompositeDto dto =
                    ProductGroupDescriptionCompositeDtoFixtures.defaultCompositeDto(productGroupId);
            DescriptionPublishStatusResult expected =
                    new DescriptionPublishStatusResult(
                            productGroupId, 100L, "PENDING", null, 0, 0, 0, 0, List.of());

            given(compositeRepository.findByProductGroupId(productGroupId))
                    .willReturn(Optional.of(dto));
            given(compositeMapper.toResult(dto)).willReturn(expected);

            // when
            queryAdapter.findPublishStatus(productGroupId);

            // then
            then(compositeRepository).should(only()).findByProductGroupId(productGroupId);
            then(compositeMapper).should(only()).toResult(dto);
        }

        @Test
        @DisplayName("PUBLISHED 상태 상세설명이 존재할 때 Mapper 결과를 그대로 반환합니다")
        void findPublishStatus_WithPublishedDescription_ReturnsMappedResult() {
            // given
            Long productGroupId = 3L;
            DescriptionCompositeDto dto =
                    ProductGroupDescriptionCompositeDtoFixtures.publishedCompositeDto(
                            productGroupId);

            DescriptionPublishStatusResult expected =
                    new DescriptionPublishStatusResult(
                            productGroupId,
                            ProductGroupDescriptionCompositeDtoFixtures.DEFAULT_DESCRIPTION_ID,
                            "PUBLISHED",
                            ProductGroupDescriptionCompositeDtoFixtures.DEFAULT_CDN_PATH,
                            2,
                            2,
                            0,
                            0,
                            List.of());

            given(compositeRepository.findByProductGroupId(productGroupId))
                    .willReturn(Optional.of(dto));
            given(compositeMapper.toResult(dto)).willReturn(expected);

            // when
            DescriptionPublishStatusResult result = queryAdapter.findPublishStatus(productGroupId);

            // then
            assertThat(result.publishStatus()).isEqualTo("PUBLISHED");
            assertThat(result.cdnPath())
                    .isEqualTo(ProductGroupDescriptionCompositeDtoFixtures.DEFAULT_CDN_PATH);
        }
    }
}
