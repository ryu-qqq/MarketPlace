package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyDescriptionImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyDescriptionImageJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyProductGroupDetailDescriptionJpaRepository;
import com.ryuqq.marketplace.domain.legacy.productdescription.aggregate.LegacyProductGroupDescription;
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
 * <p>PER-ADP-001: QueryAdapter는 JpaRepository를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductGroupDescriptionReadAdapter 단위 테스트")
class LegacyProductGroupDescriptionReadAdapterTest {

    @Mock private LegacyProductGroupDetailDescriptionJpaRepository descriptionRepository;

    @Mock private LegacyDescriptionImageJpaRepository imageRepository;

    @Mock private LegacyProductCommandEntityMapper mapper;

    @InjectMocks private LegacyProductGroupDescriptionReadAdapter readAdapter;

    @Nested
    @DisplayName("findByProductGroupId 메서드 테스트")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("상세설명이 있는 상품그룹 ID로 조회 시 LegacyProductGroupDescription을 반환합니다")
        void findByProductGroupId_WithExistingDescription_ReturnsDescription() {
            // given
            long productGroupId = 1L;
            LegacyProductGroupDetailDescriptionEntity descEntity =
                    LegacyProductGroupDetailDescriptionEntity.create(1L, "<p>상세설명</p>");
            List<LegacyDescriptionImageEntity> imageEntities = List.of();
            LegacyProductGroupDescription domain =
                    LegacyProductGroupDescription.forNew(1L, "<p>상세설명</p>");

            given(descriptionRepository.findById(productGroupId))
                    .willReturn(Optional.of(descEntity));
            given(imageRepository.findAllByProductGroupIdAndDeletedFalse(productGroupId))
                    .willReturn(imageEntities);
            given(mapper.toDescriptionDomain(descEntity, imageEntities)).willReturn(domain);

            // when
            Optional<LegacyProductGroupDescription> result =
                    readAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().productGroupId()).isEqualTo(1L);
            then(descriptionRepository).should().findById(productGroupId);
            then(imageRepository).should().findAllByProductGroupIdAndDeletedFalse(productGroupId);
            then(mapper).should().toDescriptionDomain(descEntity, imageEntities);
        }

        @Test
        @DisplayName("상세설명이 없는 상품그룹 ID로 조회 시 빈 Optional을 반환합니다")
        void findByProductGroupId_WithNoDescription_ReturnsEmpty() {
            // given
            long productGroupId = 999L;

            given(descriptionRepository.findById(productGroupId)).willReturn(Optional.empty());

            // when
            Optional<LegacyProductGroupDescription> result =
                    readAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
            then(descriptionRepository).should().findById(productGroupId);
            then(imageRepository).shouldHaveNoInteractions();
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
