package com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.OutboundProductImageJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.entity.OutboundProductImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.mapper.OutboundProductImageJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.repository.OutboundProductImageQueryDslRepository;
import com.ryuqq.marketplace.domain.outboundproductimage.OutboundProductImageFixtures;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
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
 * OutboundProductImageQueryAdapterTest - Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDSL Repository를 사용합니다.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("OutboundProductImageQueryAdapter 단위 테스트")
class OutboundProductImageQueryAdapterTest {

    @Mock private OutboundProductImageQueryDslRepository queryDslRepository;

    @Mock private OutboundProductImageJpaEntityMapper mapper;

    @InjectMocks private OutboundProductImageQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findActiveByOutboundProductId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findActiveByOutboundProductId 메서드 테스트")
    class FindActiveByOutboundProductIdTest {

        @Test
        @DisplayName("outboundProductId로 활성 이미지 목록을 반환합니다")
        void findActiveByOutboundProductId_WithValidId_ReturnsDomainList() {
            // given
            Long outboundProductId = 100L;
            OutboundProductImageJpaEntity entity1 =
                    OutboundProductImageJpaEntityFixtures.entityWithId(1L);
            OutboundProductImageJpaEntity entity2 =
                    OutboundProductImageJpaEntityFixtures.entityWithId(2L);
            OutboundProductImage domain1 =
                    OutboundProductImageFixtures.activeThumbnailImage();
            OutboundProductImage domain2 =
                    OutboundProductImageFixtures.activeDetailImage(2L, 1);

            given(queryDslRepository.findActiveByOutboundProductId(outboundProductId))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<OutboundProductImage> result =
                    queryAdapter.findActiveByOutboundProductId(outboundProductId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("활성 이미지가 없을 때 빈 목록을 반환합니다")
        void findActiveByOutboundProductId_WithNoResults_ReturnsEmptyList() {
            // given
            Long outboundProductId = 999L;

            given(queryDslRepository.findActiveByOutboundProductId(outboundProductId))
                    .willReturn(List.of());

            // when
            List<OutboundProductImage> result =
                    queryAdapter.findActiveByOutboundProductId(outboundProductId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("QueryDSL Repository가 정확히 한 번 호출됩니다")
        void findActiveByOutboundProductId_CallsRepositoryOnce() {
            // given
            Long outboundProductId = 100L;

            given(queryDslRepository.findActiveByOutboundProductId(outboundProductId))
                    .willReturn(List.of());

            // when
            queryAdapter.findActiveByOutboundProductId(outboundProductId);

            // then
            then(queryDslRepository)
                    .should()
                    .findActiveByOutboundProductId(outboundProductId);
        }

        @Test
        @DisplayName("각 Entity에 대해 Mapper가 호출됩니다")
        void findActiveByOutboundProductId_CallsMapperForEachEntity() {
            // given
            Long outboundProductId = 100L;
            OutboundProductImageJpaEntity entity1 =
                    OutboundProductImageJpaEntityFixtures.entityWithId(1L);
            OutboundProductImageJpaEntity entity2 =
                    OutboundProductImageJpaEntityFixtures.entityWithId(2L);
            OutboundProductImageJpaEntity entity3 =
                    OutboundProductImageJpaEntityFixtures.entityWithId(3L);
            OutboundProductImage domain1 = OutboundProductImageFixtures.activeThumbnailImage();
            OutboundProductImage domain2 = OutboundProductImageFixtures.activeDetailImage(2L, 1);
            OutboundProductImage domain3 = OutboundProductImageFixtures.activeDetailImage(3L, 2);

            given(queryDslRepository.findActiveByOutboundProductId(outboundProductId))
                    .willReturn(List.of(entity1, entity2, entity3));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);
            given(mapper.toDomain(entity3)).willReturn(domain3);

            // when
            List<OutboundProductImage> result =
                    queryAdapter.findActiveByOutboundProductId(outboundProductId);

            // then
            assertThat(result).hasSize(3);
            then(mapper).should(times(1)).toDomain(entity1);
            then(mapper).should(times(1)).toDomain(entity2);
            then(mapper).should(times(1)).toDomain(entity3);
        }

        @Test
        @DisplayName("THUMBNAIL 이미지만 존재할 때 단일 목록을 반환합니다")
        void findActiveByOutboundProductId_WithSingleThumbnail_ReturnsSingleItem() {
            // given
            Long outboundProductId = 100L;
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.newThumbnailEntity();
            OutboundProductImage domain = OutboundProductImageFixtures.activeThumbnailImage();

            given(queryDslRepository.findActiveByOutboundProductId(outboundProductId))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<OutboundProductImage> result =
                    queryAdapter.findActiveByOutboundProductId(outboundProductId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).isThumbnail()).isTrue();
        }

        @Test
        @DisplayName("반환된 이미지 목록은 deleted=false인 항목만 포함합니다")
        void findActiveByOutboundProductId_ReturnsOnlyActiveImages() {
            // given
            Long outboundProductId = 100L;
            OutboundProductImageJpaEntity activeEntity =
                    OutboundProductImageJpaEntityFixtures.newThumbnailEntity();
            OutboundProductImage activeDomain = OutboundProductImageFixtures.activeThumbnailImage();

            given(queryDslRepository.findActiveByOutboundProductId(outboundProductId))
                    .willReturn(List.of(activeEntity));
            given(mapper.toDomain(activeEntity)).willReturn(activeDomain);

            // when
            List<OutboundProductImage> result =
                    queryAdapter.findActiveByOutboundProductId(outboundProductId);

            // then
            assertThat(result).allMatch(image -> !image.isDeleted());
        }
    }
}
