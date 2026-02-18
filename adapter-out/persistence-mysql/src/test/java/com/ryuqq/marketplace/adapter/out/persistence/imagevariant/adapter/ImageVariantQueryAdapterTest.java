package com.ryuqq.marketplace.adapter.out.persistence.imagevariant.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.ImageVariantJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.entity.ImageVariantJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.mapper.ImageVariantJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.repository.ImageVariantQueryDslRepository;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.ImageVariantFixtures;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
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
 * ImageVariantQueryAdapterTest - 이미지 Variant Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ImageVariantQueryAdapter 단위 테스트")
class ImageVariantQueryAdapterTest {

    @Mock private ImageVariantQueryDslRepository queryDslRepository;

    @Mock private ImageVariantJpaEntityMapper mapper;

    @InjectMocks private ImageVariantQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findBySourceImageId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySourceImageId 메서드 테스트")
    class FindBySourceImageIdTest {

        @Test
        @DisplayName("소스 이미지 ID와 타입으로 Variant 목록을 반환합니다")
        void findBySourceImageId_WithValidArgs_ReturnsDomainList() {
            // given
            Long sourceImageId = 100L;
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;
            ImageVariantJpaEntity entity1 = ImageVariantJpaEntityFixtures.entityWithId(1L);
            ImageVariantJpaEntity entity2 = ImageVariantJpaEntityFixtures.entityWithId(2L);
            ImageVariant domain1 = ImageVariantFixtures.reconstitutedVariant(1L);
            ImageVariant domain2 = ImageVariantFixtures.reconstitutedVariant(2L);

            given(queryDslRepository.findBySourceImageId(sourceImageId, sourceType))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ImageVariant> result = queryAdapter.findBySourceImageId(sourceImageId, sourceType);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("Variant가 없을 때 빈 목록을 반환합니다")
        void findBySourceImageId_WithNoResults_ReturnsEmptyList() {
            // given
            Long sourceImageId = 999L;
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            given(queryDslRepository.findBySourceImageId(sourceImageId, sourceType))
                    .willReturn(List.of());

            // when
            List<ImageVariant> result = queryAdapter.findBySourceImageId(sourceImageId, sourceType);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("QueryDslRepository가 정확히 한 번 호출됩니다")
        void findBySourceImageId_CallsRepositoryOnce() {
            // given
            Long sourceImageId = 100L;
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            given(queryDslRepository.findBySourceImageId(sourceImageId, sourceType))
                    .willReturn(List.of());

            // when
            queryAdapter.findBySourceImageId(sourceImageId, sourceType);

            // then
            then(queryDslRepository).should().findBySourceImageId(sourceImageId, sourceType);
        }

        @Test
        @DisplayName("DESCRIPTION_IMAGE 소스 타입으로 Variant를 조회합니다")
        void findBySourceImageId_WithDescriptionImageType_ReturnsDomainList() {
            // given
            Long sourceImageId = 200L;
            ImageSourceType sourceType = ImageSourceType.DESCRIPTION_IMAGE;
            ImageVariantJpaEntity entity =
                    ImageVariantJpaEntityFixtures.newDescriptionImageEntity();
            ImageVariant domain = ImageVariantFixtures.reconstitutedVariant(1L);

            given(queryDslRepository.findBySourceImageId(sourceImageId, sourceType))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ImageVariant> result = queryAdapter.findBySourceImageId(sourceImageId, sourceType);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("각 Entity에 대해 Mapper가 호출됩니다")
        void findBySourceImageId_CallsMapperForEachEntity() {
            // given
            Long sourceImageId = 100L;
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;
            ImageVariantJpaEntity entity1 = ImageVariantJpaEntityFixtures.entityWithId(1L);
            ImageVariantJpaEntity entity2 = ImageVariantJpaEntityFixtures.entityWithId(2L);
            ImageVariantJpaEntity entity3 = ImageVariantJpaEntityFixtures.entityWithId(3L);
            ImageVariant domain1 = ImageVariantFixtures.reconstitutedVariant(1L);
            ImageVariant domain2 = ImageVariantFixtures.reconstitutedVariant(2L);
            ImageVariant domain3 = ImageVariantFixtures.reconstitutedVariant(3L);

            given(queryDslRepository.findBySourceImageId(sourceImageId, sourceType))
                    .willReturn(List.of(entity1, entity2, entity3));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);
            given(mapper.toDomain(entity3)).willReturn(domain3);

            // when
            List<ImageVariant> result = queryAdapter.findBySourceImageId(sourceImageId, sourceType);

            // then
            assertThat(result).hasSize(3);
            then(mapper).should().toDomain(entity1);
            then(mapper).should().toDomain(entity2);
            then(mapper).should().toDomain(entity3);
        }
    }
}
