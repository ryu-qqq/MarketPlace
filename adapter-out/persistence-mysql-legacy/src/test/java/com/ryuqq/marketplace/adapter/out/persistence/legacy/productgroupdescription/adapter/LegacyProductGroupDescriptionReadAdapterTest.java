package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyDescriptionImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.mapper.LegacyProductGroupDescriptionEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyProductGroupDescriptionQueryDslRepository;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupDescriptionId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import java.time.Instant;
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
 * LegacyProductGroupDescriptionReadAdapterTest - 레거시 상품그룹 상세설명 Read Adapter 단위 테스트.
 *
 * <p>PER-ADP-002: QueryAdapter는 QueryDslRepository를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductGroupDescriptionReadAdapter 단위 테스트")
class LegacyProductGroupDescriptionReadAdapterTest {

    @Mock private LegacyProductGroupDescriptionQueryDslRepository queryDslRepository;

    @Mock private LegacyProductGroupDescriptionEntityMapper mapper;

    @InjectMocks private LegacyProductGroupDescriptionReadAdapter readAdapter;

    @Nested
    @DisplayName("findByProductGroupId 메서드 테스트")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("상세설명이 있는 상품그룹 ID로 조회 시 ProductGroupDescription을 반환합니다")
        void findByProductGroupId_WithExistingDescription_ReturnsDescription() {
            // given
            long productGroupId = 1L;
            LegacyProductGroupDetailDescriptionEntity descEntity =
                    LegacyProductGroupDetailDescriptionEntity.createFull(
                            1L, "<p>상세설명</p>", null, "PENDING");
            List<LegacyDescriptionImageEntity> imageEntities = List.of();
            ProductGroupDescription domain =
                    ProductGroupDescription.reconstitute(
                            ProductGroupDescriptionId.of(1L),
                            ProductGroupId.of(1L),
                            DescriptionHtml.of("<p>상세설명</p>"),
                            null,
                            DescriptionPublishStatus.PENDING,
                            List.of(),
                            Instant.now(),
                            Instant.now());

            given(queryDslRepository.findDescriptionByProductGroupId(productGroupId))
                    .willReturn(Optional.of(descEntity));
            given(queryDslRepository.findImagesByProductGroupId(productGroupId))
                    .willReturn(imageEntities);
            given(mapper.toDomain(descEntity, imageEntities)).willReturn(domain);

            // when
            Optional<ProductGroupDescription> result =
                    readAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().productGroupIdValue()).isEqualTo(1L);
            then(queryDslRepository).should().findDescriptionByProductGroupId(productGroupId);
            then(queryDslRepository).should().findImagesByProductGroupId(productGroupId);
            then(mapper).should().toDomain(descEntity, imageEntities);
        }

        @Test
        @DisplayName("상세설명이 없는 상품그룹 ID로 조회 시 빈 Optional을 반환합니다")
        void findByProductGroupId_WithNoDescription_ReturnsEmpty() {
            // given
            long productGroupId = 999L;

            given(queryDslRepository.findDescriptionByProductGroupId(productGroupId))
                    .willReturn(Optional.empty());

            // when
            Optional<ProductGroupDescription> result =
                    readAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findDescriptionByProductGroupId(productGroupId);
        }
    }
}
