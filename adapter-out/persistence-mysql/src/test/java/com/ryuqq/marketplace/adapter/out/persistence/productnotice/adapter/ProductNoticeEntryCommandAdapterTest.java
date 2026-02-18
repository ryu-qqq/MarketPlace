package com.ryuqq.marketplace.adapter.out.persistence.productnotice.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.ProductNoticeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.mapper.ProductNoticeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository.ProductNoticeEntryJpaRepository;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ProductNoticeEntryCommandAdapterTest - 상품 고시정보 항목 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductNoticeEntryCommandAdapter 단위 테스트")
class ProductNoticeEntryCommandAdapterTest {

    @Mock private ProductNoticeEntryJpaRepository repository;

    @Mock private ProductNoticeJpaEntityMapper mapper;

    @InjectMocks private ProductNoticeEntryCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("ProductNoticeEntry를 Entity로 변환 후 저장합니다")
        void persist_WithValidEntry_SavesEntry() {
            // given
            ProductNoticeEntry entry = ProductNoticeFixtures.defaultEntry();
            ProductNoticeEntryJpaEntity entity =
                    ProductNoticeJpaEntityFixtures.defaultEntryEntity(1L);

            given(mapper.toEntryEntity(entry)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(entry);

            // then
            then(mapper).should().toEntryEntity(entry);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("특정 값의 Entry를 저장합니다")
        void persist_WithSpecificEntry_Saves() {
            // given
            ProductNoticeEntry entry = ProductNoticeFixtures.entry(100L, "대한민국");
            ProductNoticeEntryJpaEntity entity =
                    ProductNoticeJpaEntityFixtures.entryEntity(1L, 100L, "대한민국");

            given(mapper.toEntryEntity(entry)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(entry);

            // then
            then(mapper).should().toEntryEntity(entry);
            then(repository).should().save(entity);
        }
    }
}
