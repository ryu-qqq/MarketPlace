package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupQueryDslRepository;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ManagementType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.Origin;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ProductCondition;
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
 * LegacyProductGroupQueryAdapterTest - 레거시 상품그룹 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-002: QueryAdapter는 QueryDslRepository를 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductGroupQueryAdapter 단위 테스트")
class LegacyProductGroupQueryAdapterTest {

    @Mock private LegacyProductGroupQueryDslRepository queryDslRepository;

    @Mock private LegacyProductCommandEntityMapper mapper;

    @InjectMocks private LegacyProductGroupQueryAdapter queryAdapter;

    private LegacyProductGroupEntity buildEntity() {
        return LegacyProductGroupEntity.create(
                1L,
                "테스트상품",
                10L,
                20L,
                30L,
                "SINGLE",
                "MENUAL",
                50000L,
                45000L,
                "N",
                "Y",
                "NEW",
                "KR",
                "STYLE001");
    }

    private LegacyProductGroup buildDomain() {
        return LegacyProductGroup.reconstitute(
                1L,
                "테스트상품",
                10L,
                20L,
                30L,
                OptionType.SINGLE,
                ManagementType.MENUAL,
                50000L,
                45000L,
                "N",
                "Y",
                ProductCondition.NEW,
                Origin.KR,
                "STYLE001",
                null,
                null);
    }

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 상품그룹을 반환합니다")
        void findById_WithExistingId_ReturnsProductGroup() {
            // given
            LegacyProductGroupId productGroupId = LegacyProductGroupId.of(1L);
            LegacyProductGroupEntity entity = buildEntity();
            LegacyProductGroup domain = buildDomain();

            given(queryDslRepository.findProductGroupById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<LegacyProductGroup> result = queryAdapter.findById(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().idValue()).isEqualTo(1L);
            then(queryDslRepository).should().findProductGroupById(1L);
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // given
            LegacyProductGroupId productGroupId = LegacyProductGroupId.of(999L);

            given(queryDslRepository.findProductGroupById(999L)).willReturn(Optional.empty());

            // when
            Optional<LegacyProductGroup> result = queryAdapter.findById(productGroupId);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findProductGroupById(999L);
        }
    }
}
