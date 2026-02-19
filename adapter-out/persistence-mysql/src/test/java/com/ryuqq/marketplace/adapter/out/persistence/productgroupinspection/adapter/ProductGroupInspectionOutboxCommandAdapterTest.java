package com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.ProductGroupInspectionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.entity.ProductGroupInspectionOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.mapper.ProductGroupInspectionOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.repository.ProductGroupInspectionOutboxJpaRepository;
import com.ryuqq.marketplace.domain.productgroupinspection.ProductGroupInspectionFixtures;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ProductGroupInspectionOutboxCommandAdapterTest - 상품 그룹 검수 Outbox Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupInspectionOutboxCommandAdapter 단위 테스트")
class ProductGroupInspectionOutboxCommandAdapterTest {

    @Mock private ProductGroupInspectionOutboxJpaRepository repository;

    @Mock private ProductGroupInspectionOutboxJpaEntityMapper mapper;

    @InjectMocks private ProductGroupInspectionOutboxCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Domain을 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidDomain_SavesAndReturnsId() {
            // given
            ProductGroupInspectionOutbox domain = ProductGroupInspectionFixtures.newPendingOutbox();
            ProductGroupInspectionOutboxJpaEntity entityToSave =
                    ProductGroupInspectionOutboxJpaEntityFixtures.newPendingEntity();
            ProductGroupInspectionOutboxJpaEntity savedEntity =
                    ProductGroupInspectionOutboxJpaEntityFixtures.pendingEntity(100L);

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
        @DisplayName("PENDING 상태 Outbox를 저장합니다")
        void persist_WithPendingOutbox_Saves() {
            // given
            ProductGroupInspectionOutbox domain = ProductGroupInspectionFixtures.newPendingOutbox();
            ProductGroupInspectionOutboxJpaEntity entityToSave =
                    ProductGroupInspectionOutboxJpaEntityFixtures.newPendingEntity();
            ProductGroupInspectionOutboxJpaEntity savedEntity =
                    ProductGroupInspectionOutboxJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            ProductGroupInspectionOutbox domain = ProductGroupInspectionFixtures.newPendingOutbox();
            ProductGroupInspectionOutboxJpaEntity entity =
                    ProductGroupInspectionOutboxJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }

        @Test
        @DisplayName("Repository의 save가 정확히 한 번 호출됩니다")
        void persist_CallsRepositorySaveOnce() {
            // given
            ProductGroupInspectionOutbox domain = ProductGroupInspectionFixtures.newPendingOutbox();
            ProductGroupInspectionOutboxJpaEntity entity =
                    ProductGroupInspectionOutboxJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(domain)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(repository).should(times(1)).save(entity);
        }
    }
}
