package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.ProductGroupDescriptionJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper.ProductGroupDescriptionJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.DescriptionImageQueryDslRepository;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
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
 * DescriptionImageQueryAdapterTest - 상세설명 이미지 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DescriptionImageQueryAdapter 단위 테스트")
class DescriptionImageQueryAdapterTest {

    @Mock private DescriptionImageQueryDslRepository queryDslRepository;

    @Mock private ProductGroupDescriptionJpaEntityMapper mapper;

    @InjectMocks private DescriptionImageQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            Long id = 1L;
            DescriptionImageJpaEntity entity =
                    ProductGroupDescriptionJpaEntityFixtures.imageEntity(id, 10L);
            DescriptionImage domain = ProductGroupFixtures.defaultDescriptionImage();

            given(queryDslRepository.findById(id)).willReturn(Optional.of(entity));
            given(mapper.toImageDomain(entity)).willReturn(domain);

            // when
            Optional<DescriptionImage> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findById(id);
            then(mapper).should().toImageDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            Long id = 999L;
            given(queryDslRepository.findById(id)).willReturn(Optional.empty());

            // when
            Optional<DescriptionImage> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findById(id);
            then(mapper).shouldHaveNoInteractions();
        }
    }
}
