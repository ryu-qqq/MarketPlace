package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.LegacyOptionGroupEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.mapper.LegacyOptionCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.repository.LegacyOptionGroupJpaRepository;
import com.ryuqq.marketplace.domain.legacy.optiongroup.aggregate.LegacyOptionGroup;
import com.ryuqq.marketplace.domain.legacy.optiongroup.vo.LegacyOptionName;
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

    @Mock private LegacyOptionCommandEntityMapper mapper;

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
            LegacyOptionGroup optionGroup = LegacyOptionGroup.forNew(LegacyOptionName.COLOR);

            LegacyOptionGroupEntity entity = LegacyOptionGroupEntityFixtures.defaultEntity();
            LegacyOptionGroupEntity savedEntity =
                    LegacyOptionGroupEntityFixtures.entityWithName("COLOR");

            given(mapper.toEntity(optionGroup)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(optionGroup);

            // then
            then(mapper).should().toEntity(optionGroup);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper와 Repository가 순서대로 호출됩니다")
        void persist_CallsMapperThenRepository() {
            // given
            LegacyOptionGroup optionGroup = LegacyOptionGroup.forNew(LegacyOptionName.SIZE);
            LegacyOptionGroupEntity entity = LegacyOptionGroupEntityFixtures.sizeEntity();

            given(mapper.toEntity(optionGroup)).willReturn(entity);
            given(repository.save(any())).willReturn(entity);

            // when
            commandAdapter.persist(optionGroup);

            // then
            then(mapper).should().toEntity(optionGroup);
            then(repository).should().save(entity);
        }
    }
}
