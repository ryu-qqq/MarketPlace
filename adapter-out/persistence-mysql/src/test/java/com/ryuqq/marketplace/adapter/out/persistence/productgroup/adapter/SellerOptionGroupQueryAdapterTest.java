package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.ProductGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.SellerOptionGroupQueryDslRepository;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
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
 * SellerOptionGroupQueryAdapterTest - 셀러 옵션 그룹 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용. PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerOptionGroupQueryAdapter 단위 테스트")
class SellerOptionGroupQueryAdapterTest {

    @Mock private SellerOptionGroupQueryDslRepository queryDslRepository;

    @Mock private ProductGroupJpaEntityMapper mapper;

    @InjectMocks private SellerOptionGroupQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupId 메서드 테스트")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("ProductGroupId로 SellerOptionGroup 목록을 조회합니다")
        void findByProductGroupId_WithValidProductGroupId_ReturnsOptionGroups() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            // non-null ID를 가진 entity 사용 (groupId=10L)
            SellerOptionGroupJpaEntity groupEntity =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(10L, 1L);
            SellerOptionValueJpaEntity valueEntity =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(100L, 10L);
            SellerOptionValue domainValue = ProductGroupFixtures.defaultSellerOptionValue();
            SellerOptionGroup domainGroup = ProductGroupFixtures.defaultSellerOptionGroup();

            given(queryDslRepository.findByProductGroupId(1L)).willReturn(List.of(groupEntity));
            given(queryDslRepository.findValuesByGroupIds(List.of(10L)))
                    .willReturn(List.of(valueEntity));
            given(mapper.toOptionValueDomain(valueEntity)).willReturn(domainValue);
            given(mapper.toOptionGroupDomain(groupEntity, List.of(domainValue)))
                    .willReturn(domainGroup);

            // when
            List<SellerOptionGroup> result = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(1);
            then(queryDslRepository).should().findByProductGroupId(1L);
            then(queryDslRepository).should().findValuesByGroupIds(List.of(10L));
        }

        @Test
        @DisplayName("옵션 그룹이 없으면 빈 리스트를 반환합니다")
        void findByProductGroupId_WithNoGroups_ReturnsEmptyList() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(999L);
            given(queryDslRepository.findByProductGroupId(999L)).willReturn(List.of());
            given(queryDslRepository.findValuesByGroupIds(List.of())).willReturn(List.of());

            // when
            List<SellerOptionGroup> result = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 옵션 그룹과 각 그룹의 값들을 올바르게 매핑합니다")
        void findByProductGroupId_WithMultipleGroups_MapsCorrectly() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            // non-null ID를 가진 서로 다른 entity 사용
            SellerOptionGroupJpaEntity groupEntity1 =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(10L, 1L);
            SellerOptionGroupJpaEntity groupEntity2 =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(20L, 1L);

            SellerOptionValueJpaEntity valueForGroup1 =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(101L, 10L);
            SellerOptionValueJpaEntity valueForGroup2 =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(201L, 20L);

            SellerOptionValue domainValue1 = ProductGroupFixtures.defaultSellerOptionValue();
            SellerOptionValue domainValue2 = ProductGroupFixtures.mappedSellerOptionValue();
            SellerOptionGroup domainGroup1 = ProductGroupFixtures.defaultSellerOptionGroup();
            SellerOptionGroup domainGroup2 = ProductGroupFixtures.mappedSellerOptionGroup();

            List<Long> groupIds = List.of(10L, 20L);
            given(queryDslRepository.findByProductGroupId(1L))
                    .willReturn(List.of(groupEntity1, groupEntity2));
            given(queryDslRepository.findValuesByGroupIds(groupIds))
                    .willReturn(List.of(valueForGroup1, valueForGroup2));
            given(mapper.toOptionValueDomain(valueForGroup1)).willReturn(domainValue1);
            given(mapper.toOptionValueDomain(valueForGroup2)).willReturn(domainValue2);
            given(mapper.toOptionGroupDomain(groupEntity1, List.of(domainValue1)))
                    .willReturn(domainGroup1);
            given(mapper.toOptionGroupDomain(groupEntity2, List.of(domainValue2)))
                    .willReturn(domainGroup2);

            // when
            List<SellerOptionGroup> result = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByProductGroupId(1L);
            then(queryDslRepository).should().findValuesByGroupIds(groupIds);
        }

        @Test
        @DisplayName("옵션 그룹이 있지만 값이 없는 경우 빈 값 목록으로 매핑합니다")
        void findByProductGroupId_WithGroupButNoValues_MapsGroupWithEmptyValues() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            // non-null ID를 가진 entity 사용 (groupId=10L)
            SellerOptionGroupJpaEntity groupEntity =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(10L, 1L);
            SellerOptionGroup domainGroup = ProductGroupFixtures.defaultSellerOptionGroup();

            given(queryDslRepository.findByProductGroupId(1L)).willReturn(List.of(groupEntity));
            given(queryDslRepository.findValuesByGroupIds(List.of(10L))).willReturn(List.of());
            given(mapper.toOptionGroupDomain(groupEntity, List.of())).willReturn(domainGroup);

            // when
            List<SellerOptionGroup> result = queryAdapter.findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(1);
            then(mapper).should().toOptionGroupDomain(groupEntity, List.of());
        }
    }
}
