package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.LegacyOptionDetailEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.mapper.LegacyOptionCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.repository.LegacyOptionDetailJpaRepository;
import com.ryuqq.marketplace.domain.legacy.optiondetail.aggregate.LegacyOptionDetail;
import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyOptionDetailCommandAdapterTest - 레거시 옵션 상세 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyOptionDetailCommandAdapter 단위 테스트")
class LegacyOptionDetailCommandAdapterTest {

    @Mock private LegacyOptionDetailJpaRepository repository;

    @Mock private LegacyOptionCommandEntityMapper mapper;

    @InjectMocks private LegacyOptionDetailCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("옵션 상세를 저장하고 ID를 반환합니다")
        void persist_WithValidOptionDetail_ReturnsSavedId() {
            // given
            LegacyOptionDetail optionDetail =
                    LegacyOptionDetail.forNew(LegacyOptionGroupId.of(1L), "RED");

            LegacyOptionDetailEntity entity = LegacyOptionDetailEntityFixtures.defaultEntity();
            LegacyOptionDetailEntity savedEntity =
                    LegacyOptionDetailEntityFixtures.entityWithGroupIdAndValue(1L, "RED");

            given(mapper.toEntity(optionDetail)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            commandAdapter.persist(optionDetail);

            // then
            then(mapper).should().toEntity(optionDetail);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("다른 옵션 값으로도 저장이 가능합니다")
        void persist_WithDifferentOptionValue_SavesSuccessfully() {
            // given
            LegacyOptionDetail optionDetail =
                    LegacyOptionDetail.forNew(LegacyOptionGroupId.of(2L), "BLUE");

            LegacyOptionDetailEntity entity =
                    LegacyOptionDetailEntityFixtures.entityWithGroupIdAndValue(2L, "BLUE");

            given(mapper.toEntity(optionDetail)).willReturn(entity);
            given(repository.save(any())).willReturn(entity);

            // when
            commandAdapter.persist(optionDetail);

            // then
            then(repository).should().save(entity);
        }
    }
}
