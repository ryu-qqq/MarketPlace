package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.mapper.LegacyProductGroupImageEntityMapper;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.id.ProductGroupImageId;
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
 * LegacyProductImageQueryAdapterTest - 레거시 상품 이미지 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-002: QueryAdapter는 QueryDslRepository를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductImageQueryAdapter 단위 테스트")
class LegacyProductImageQueryAdapterTest {

    @Mock private LegacyProductGroupQueryDslRepository queryDslRepository;

    @Mock private LegacyProductGroupImageEntityMapper mapper;

    @InjectMocks private LegacyProductImageQueryAdapter queryAdapter;

    private LegacyProductGroupImageEntity buildImageEntity() {
        return LegacyProductGroupImageEntity.create(
                1L,
                1L,
                "THUMBNAIL",
                "https://cdn.example.com/image.jpg",
                "https://origin.example.com/image.jpg",
                1L,
                "N");
    }

    private ProductGroupImage buildImageDomain() {
        return ProductGroupImage.reconstitute(
                ProductGroupImageId.of(1L),
                ProductGroupId.of(1L),
                ImageUrl.of("https://origin.example.com/image.jpg"),
                ImageUrl.of("https://cdn.example.com/image.jpg"),
                ImageType.THUMBNAIL,
                1,
                DeletionStatus.active());
    }

    @Nested
    @DisplayName("findByProductGroupId 메서드 테스트")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("상품그룹 ID로 조회 시 이미지 목록을 반환합니다")
        void findByProductGroupId_WithExistingId_ReturnsImages() {
            // given
            long productGroupId = 1L;
            LegacyProductGroupImageEntity entity = buildImageEntity();
            ProductGroupImage domain = buildImageDomain();

            given(queryDslRepository.findImagesByProductGroupId(1L)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ProductGroupImage> results = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).imageType()).isEqualTo(ImageType.THUMBNAIL);
            then(queryDslRepository).should().findImagesByProductGroupId(1L);
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("이미지가 없는 상품그룹 ID로 조회 시 빈 목록을 반환합니다")
        void findByProductGroupId_WithNoImages_ReturnsEmptyList() {
            // given
            long productGroupId = 999L;

            given(queryDslRepository.findImagesByProductGroupId(999L)).willReturn(List.of());

            // when
            List<ProductGroupImage> results = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(results).isEmpty();
            then(queryDslRepository).should().findImagesByProductGroupId(999L);
        }
    }
}
