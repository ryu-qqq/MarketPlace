package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper.SellerAdminJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository.SellerAdminJpaRepository;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SellerAdminCommandAdapterTest - 셀러 관리자 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-002: Adapter에서 @Transactional 금지.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SellerAdminCommandAdapter 단위 테스트")
class SellerAdminCommandAdapterTest {

    @Mock private SellerAdminJpaRepository jpaRepository;

    @Mock private SellerAdminJpaEntityMapper mapper;

    @InjectMocks private SellerAdminCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("새로운 셀러 관리자 저장 시 ID를 반환합니다")
        void persist_WithNewSellerAdmin_ReturnsId() {
            // given
            SellerAdmin domain = SellerFixtures.newSellerAdmin();
            SellerAdminJpaEntity entityToSave = SellerAdminJpaEntityFixtures.activeEntity();
            SellerAdminJpaEntity savedEntity = SellerAdminJpaEntityFixtures.activeEntity();

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            String result = commandAdapter.persist(domain);

            // then
            assertThat(result).isEqualTo(SellerAdminJpaEntityFixtures.DEFAULT_ID);
            then(mapper).should().toEntity(domain);
            then(jpaRepository).should().save(entityToSave);
        }

        @Test
        @DisplayName("활성 상태 셀러 관리자 저장 시 ID를 반환합니다")
        void persist_WithActiveSellerAdmin_ReturnsId() {
            // given
            SellerAdmin domain = SellerFixtures.activeSellerAdmin();
            SellerAdminJpaEntity entityToSave = SellerAdminJpaEntityFixtures.activeEntity();
            SellerAdminJpaEntity savedEntity = SellerAdminJpaEntityFixtures.activeEntity();

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            String result = commandAdapter.persist(domain);

            // then
            assertThat(result).isEqualTo(savedEntity.getId());
        }

        @Test
        @DisplayName("승인 대기 상태 셀러 관리자 저장 시 ID를 반환합니다")
        void persist_WithPendingApprovalSellerAdmin_ReturnsId() {
            // given
            SellerAdmin domain = SellerFixtures.pendingApprovalSellerAdmin();
            SellerAdminJpaEntity entityToSave =
                    SellerAdminJpaEntityFixtures.pendingApprovalEntity();
            SellerAdminJpaEntity savedEntity = SellerAdminJpaEntityFixtures.pendingApprovalEntity();

            given(mapper.toEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            String result = commandAdapter.persist(domain);

            // then
            assertThat(result).isEqualTo(savedEntity.getId());
        }

        @Test
        @DisplayName("Mapper와 Repository가 올바르게 호출됩니다")
        void persist_CallsMapperAndRepository() {
            // given
            SellerAdmin domain = SellerFixtures.activeSellerAdmin();
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.activeEntity();

            given(mapper.toEntity(domain)).willReturn(entity);
            given(jpaRepository.save(any(SellerAdminJpaEntity.class))).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
            then(jpaRepository).should().save(entity);
        }
    }
}
