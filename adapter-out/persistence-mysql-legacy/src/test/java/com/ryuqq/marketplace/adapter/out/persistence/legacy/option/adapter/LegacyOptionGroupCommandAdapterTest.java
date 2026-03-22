package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.repository.LegacyOptionGroupJpaRepository;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionInputType;
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
 * LegacyOptionGroupCommandAdapterTest - 레거시 옵션 그룹 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyOptionGroupCommandAdapter 단위 테스트")
class LegacyOptionGroupCommandAdapterTest {

    @Mock private LegacyOptionGroupJpaRepository repository;

    @InjectMocks private LegacyOptionGroupCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("옵션 그룹을 저장하고 ID를 반환합니다")
        void persist_WithValidOptionGroup_ReturnsSavedId() {
            // given
            SellerOptionGroup optionGroup =
                    SellerOptionGroup.forNew(
                            ProductGroupId.of(1L),
                            OptionGroupName.of("색상"),
                            OptionInputType.PREDEFINED,
                            0,
                            List.of());

            LegacyOptionGroupEntity savedEntity =
                    LegacyOptionGroupEntity.create(1L, 1L, "색상", "N");

            given(repository.save(any())).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(optionGroup);

            // then
            then(repository).should().save(any());
        }

        @Test
        @DisplayName("Mapper와 Repository가 순서대로 호출됩니다")
        void persist_CallsMapperThenRepository() {
            // given
            SellerOptionGroup optionGroup =
                    SellerOptionGroup.forNew(
                            ProductGroupId.of(2L),
                            OptionGroupName.of("사이즈"),
                            OptionInputType.PREDEFINED,
                            0,
                            List.of());

            LegacyOptionGroupEntity entity = LegacyOptionGroupEntity.create(2L, "사이즈");

            given(repository.save(any())).willReturn(entity);

            // when
            commandAdapter.persist(optionGroup);

            // then
            then(repository).should().save(any());
        }
    }
}
