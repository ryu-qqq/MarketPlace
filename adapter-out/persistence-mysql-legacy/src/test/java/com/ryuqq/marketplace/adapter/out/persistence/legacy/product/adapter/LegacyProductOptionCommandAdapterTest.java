package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductOptionJpaRepository;
import com.ryuqq.marketplace.domain.legacy.optiondetail.id.LegacyOptionDetailId;
import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
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
 * LegacyProductOptionCommandAdapterTest - 레거시 상품 옵션 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductOptionCommandAdapter 단위 테스트")
class LegacyProductOptionCommandAdapterTest {

    @Mock private LegacyProductOptionJpaRepository repository;

    @Mock private LegacyProductCommandEntityMapper mapper;

    @InjectMocks private LegacyProductOptionCommandAdapter commandAdapter;

    private LegacyProductOption buildProductOption() {
        return LegacyProductOption.forNew(
                LegacyProductId.of(1L),
                LegacyOptionGroupId.of(10L),
                LegacyOptionDetailId.of(100L),
                0L);
    }

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("상품 옵션을 저장합니다")
        void persist_WithValidProductOption_SavesSuccessfully() {
            // given
            LegacyProductOption productOption = buildProductOption();
            LegacyProductOptionEntity entity = LegacyProductOptionEntity.create(1L, 10L, 100L, 0L);

            given(mapper.toEntity(productOption)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(productOption);

            // then
            then(mapper).should().toEntity(productOption);
            then(repository).should().save(entity);
        }
    }

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 상품 옵션을 일괄 저장합니다")
        void persistAll_WithMultipleOptions_SavesAll() {
            // given
            LegacyProductOption option1 = buildProductOption();
            LegacyProductOption option2 =
                    LegacyProductOption.forNew(
                            LegacyProductId.of(2L),
                            LegacyOptionGroupId.of(20L),
                            LegacyOptionDetailId.of(200L),
                            1000L);
            List<LegacyProductOption> options = List.of(option1, option2);

            LegacyProductOptionEntity entity1 = LegacyProductOptionEntity.create(1L, 10L, 100L, 0L);
            LegacyProductOptionEntity entity2 =
                    LegacyProductOptionEntity.create(2L, 20L, 200L, 1000L);

            given(mapper.toEntity(option1)).willReturn(entity1);
            given(mapper.toEntity(option2)).willReturn(entity2);
            given(repository.saveAll(anyList())).willReturn(List.of(entity1, entity2));

            // when
            commandAdapter.persistAll(options);

            // then
            then(repository).should().saveAll(anyList());
        }

        @Test
        @DisplayName("빈 목록 저장 시 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            given(repository.saveAll(any())).willReturn(List.of());

            // when
            commandAdapter.persistAll(List.of());

            // then
            then(repository).should().saveAll(any());
        }
    }
}
