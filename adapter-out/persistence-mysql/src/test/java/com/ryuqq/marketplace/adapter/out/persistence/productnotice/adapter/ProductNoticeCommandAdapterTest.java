package com.ryuqq.marketplace.adapter.out.persistence.productnotice.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.ProductNoticeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.mapper.ProductNoticeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository.ProductNoticeJpaRepository;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ProductNoticeCommandAdapterTest - 상품 고시정보 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductNoticeCommandAdapter 단위 테스트")
class ProductNoticeCommandAdapterTest {

    @Mock private ProductNoticeJpaRepository repository;

    @Mock private ProductNoticeJpaEntityMapper mapper;

    @InjectMocks private ProductNoticeCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("ProductNotice를 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidProductNotice_SavesAndReturnsId() {
            // given
            ProductNotice domain = ProductNoticeFixtures.newProductNotice();
            ProductNoticeJpaEntity entityToSave = ProductNoticeJpaEntityFixtures.newEntity();
            ProductNoticeJpaEntity savedEntity = ProductNoticeJpaEntityFixtures.activeEntity(100L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(100L);
            then(mapper).should().toEntity(domain);
            then(repository).should().save(entityToSave);
        }

        @Test
        @DisplayName("기존 ProductNotice를 저장합니다")
        void persist_WithExistingProductNotice_Saves() {
            // given
            ProductNotice domain = ProductNoticeFixtures.existingProductNotice();
            ProductNoticeJpaEntity entityToSave = ProductNoticeJpaEntityFixtures.activeEntity(1L);
            ProductNoticeJpaEntity savedEntity = ProductNoticeJpaEntityFixtures.activeEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }
    }
}
