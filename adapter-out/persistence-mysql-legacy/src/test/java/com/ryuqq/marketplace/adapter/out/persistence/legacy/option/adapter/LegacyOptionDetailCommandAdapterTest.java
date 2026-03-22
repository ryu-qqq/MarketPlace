package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.repository.LegacyOptionDetailJpaRepository;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionValueName;
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
            SellerOptionValue optionValue =
                    SellerOptionValue.reconstitute(
                            SellerOptionValueId.of(1L),
                            SellerOptionGroupId.of(1L),
                            OptionValueName.of("RED"),
                            null,
                            0,
                            DeletionStatus.active());

            LegacyOptionDetailEntity savedEntity =
                    LegacyOptionDetailEntity.create(1L, "RED");

            given(repository.save(any())).willReturn(savedEntity);

            // when
            commandAdapter.persist(optionValue);

            // then
            then(repository).should().save(any());
        }

        @Test
        @DisplayName("다른 옵션 값으로도 저장이 가능합니다")
        void persist_WithDifferentOptionValue_SavesSuccessfully() {
            // given
            SellerOptionValue optionValue =
                    SellerOptionValue.reconstitute(
                            SellerOptionValueId.of(2L),
                            SellerOptionGroupId.of(2L),
                            OptionValueName.of("BLUE"),
                            null,
                            0,
                            DeletionStatus.active());

            LegacyOptionDetailEntity entity =
                    LegacyOptionDetailEntity.create(2L, "BLUE");

            given(repository.save(any())).willReturn(entity);

            // when
            commandAdapter.persist(optionValue);

            // then
            then(repository).should().save(any());
        }
    }
}
