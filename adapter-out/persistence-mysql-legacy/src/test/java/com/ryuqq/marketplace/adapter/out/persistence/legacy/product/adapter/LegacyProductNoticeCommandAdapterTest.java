package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductNoticeEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductNoticeJpaRepository;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductNotice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyProductNoticeCommandAdapterTest - 레거시 상품 고시정보 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductNoticeCommandAdapter 단위 테스트")
class LegacyProductNoticeCommandAdapterTest {

    @Mock private LegacyProductNoticeJpaRepository repository;

    @Mock private LegacyProductCommandEntityMapper mapper;

    @InjectMocks private LegacyProductNoticeCommandAdapter commandAdapter;

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("고시정보를 저장합니다")
        void persist_WithValidNotice_SavesSuccessfully() {
            // given
            LegacyProductGroupId productGroupId = LegacyProductGroupId.of(1L);
            LegacyProductNotice notice =
                    new LegacyProductNotice(
                            "면 100%",
                            "블랙", "M", "삼성", "한국", "물세탁", "2024-01-01", "KC인증", "1588-1234");

            LegacyProductNoticeEntity entity =
                    LegacyProductNoticeEntity.create(
                            1L,
                            "면 100%",
                            "블랙",
                            "M",
                            "삼성",
                            "한국",
                            "물세탁",
                            "2024-01-01",
                            "KC인증",
                            "1588-1234");

            given(mapper.toEntity(productGroupId, notice)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(productGroupId, notice);

            // then
            then(mapper).should().toEntity(productGroupId, notice);
            then(repository).should().save(entity);
        }
    }
}
