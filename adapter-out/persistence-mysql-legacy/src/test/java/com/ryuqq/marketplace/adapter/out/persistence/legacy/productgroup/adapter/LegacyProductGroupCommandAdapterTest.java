package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.LegacyProductGroupEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupJpaRepository;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ManagementType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.Origin;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ProductCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyProductGroupCommandAdapterTest - 레거시 상품 그룹 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductGroupCommandAdapter 단위 테스트")
class LegacyProductGroupCommandAdapterTest {

    @Mock private LegacyProductGroupJpaRepository repository;

    @Mock private LegacyProductCommandEntityMapper mapper;

    @InjectMocks private LegacyProductGroupCommandAdapter commandAdapter;

    private LegacyProductGroup buildProductGroup() {
        return LegacyProductGroup.forNew(
                "테스트 상품",
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
                null,
                null);
    }

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("상품 그룹을 저장하고 ID를 반환합니다")
        void persist_WithValidProductGroup_ReturnsSavedId() {
            // given
            LegacyProductGroup productGroup = buildProductGroup();

            LegacyProductGroupEntity entity = LegacyProductGroupEntityFixtures.newEntity();
            Long expectedId = 100L;
            LegacyProductGroupEntity savedEntity =
                    LegacyProductGroupEntityFixtures.entityWithId(expectedId);

            given(mapper.toEntity(productGroup)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(productGroup);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(mapper).should().toEntity(productGroup);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper가 먼저 호출되고 Repository가 저장합니다")
        void persist_CallsMapperBeforeRepository() {
            // given
            LegacyProductGroup productGroup = buildProductGroup();
            LegacyProductGroupEntity entity = LegacyProductGroupEntityFixtures.newEntity();
            LegacyProductGroupEntity savedEntity =
                    LegacyProductGroupEntityFixtures.entityWithId(1L);

            given(mapper.toEntity(productGroup)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            commandAdapter.persist(productGroup);

            // then (BDD 스타일 검증)
            then(mapper).should().toEntity(productGroup);
            then(repository).should().save(entity);
        }
    }
}
