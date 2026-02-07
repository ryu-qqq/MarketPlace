package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminJpaEntity;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerAdminJpaEntityMapperTest - 셀러 관리자 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("SellerAdminJpaEntityMapper 단위 테스트")
class SellerAdminJpaEntityMapperTest {

    private SellerAdminJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerAdminJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveSellerAdmin_ConvertsCorrectly() {
            // given
            SellerAdmin domain = SellerFixtures.activeSellerAdmin();

            // when
            SellerAdminJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getAuthUserId()).isEqualTo(domain.authUserId());
            assertThat(entity.getLoginId()).isEqualTo(domain.loginIdValue());
            assertThat(entity.getName()).isEqualTo(domain.nameValue());
            assertThat(entity.getPhoneNumber()).isEqualTo(domain.phoneNumberValue());
            assertThat(entity.getStatus()).isEqualTo(domain.status());
        }

        @Test
        @DisplayName("승인 대기 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithPendingApprovalSellerAdmin_ConvertsCorrectly() {
            // given
            SellerAdmin domain = SellerFixtures.pendingApprovalSellerAdmin();

            // when
            SellerAdminJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getAuthUserId()).isNull();
            assertThat(entity.getStatus())
                    .isEqualTo(
                            com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus
                                    .PENDING_APPROVAL);
        }

        @Test
        @DisplayName("거절 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithRejectedSellerAdmin_ConvertsCorrectly() {
            // given
            SellerAdmin domain = SellerFixtures.rejectedSellerAdmin();

            // when
            SellerAdminJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getAuthUserId()).isNull();
            assertThat(entity.getStatus())
                    .isEqualTo(
                            com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus.REJECTED);
        }

        @Test
        @DisplayName("정지 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithSuspendedSellerAdmin_ConvertsCorrectly() {
            // given
            SellerAdmin domain = SellerFixtures.suspendedSellerAdmin();

            // when
            SellerAdminJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus())
                    .isEqualTo(
                            com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus
                                    .SUSPENDED);
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewSellerAdmin_ConvertsCorrectly() {
            // given
            SellerAdmin domain = SellerFixtures.newSellerAdmin();

            // when
            SellerAdminJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNotNull();
            assertThat(entity.getLoginId()).isEqualTo(domain.loginIdValue());
            assertThat(entity.getStatus())
                    .isEqualTo(
                            com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus.ACTIVE);
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithActiveEntity_ConvertsCorrectly() {
            // given
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.activeEntity();

            // when
            SellerAdmin domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.authUserId()).isEqualTo(entity.getAuthUserId());
            assertThat(domain.loginIdValue()).isEqualTo(entity.getLoginId());
            assertThat(domain.nameValue()).isEqualTo(entity.getName());
            assertThat(domain.phoneNumberValue()).isEqualTo(entity.getPhoneNumber());
            assertThat(domain.status()).isEqualTo(entity.getStatus());
            assertThat(domain.isActive()).isTrue();
        }

        @Test
        @DisplayName("승인 대기 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithPendingApprovalEntity_ConvertsCorrectly() {
            // given
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.pendingApprovalEntity();

            // when
            SellerAdmin domain = mapper.toDomain(entity);

            // then
            assertThat(domain.authUserId()).isNull();
            assertThat(domain.isPendingApproval()).isTrue();
        }

        @Test
        @DisplayName("거절 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithRejectedEntity_ConvertsCorrectly() {
            // given
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.rejectedEntity();

            // when
            SellerAdmin domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isRejected()).isTrue();
        }

        @Test
        @DisplayName("정지 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithSuspendedEntity_ConvertsCorrectly() {
            // given
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.suspendedEntity();

            // when
            SellerAdmin domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status())
                    .isEqualTo(
                            com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus
                                    .SUSPENDED);
        }

        @Test
        @DisplayName("삭제된 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ConvertsCorrectly() {
            // given
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.deletedEntity();

            // when
            SellerAdmin domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("핸드폰 번호가 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutPhoneNumber_ConvertsCorrectly() {
            // given
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.entityWithoutPhoneNumber();

            // when
            SellerAdmin domain = mapper.toDomain(entity);

            // then
            assertThat(domain.phoneNumberValue()).isNull();
        }

        @Test
        @DisplayName("인증 사용자 ID가 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutAuthUserId_ConvertsCorrectly() {
            // given
            SellerAdminJpaEntity entity = SellerAdminJpaEntityFixtures.entityWithoutAuthUserId();

            // when
            SellerAdmin domain = mapper.toDomain(entity);

            // then
            assertThat(domain.authUserId()).isNull();
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesData() {
            // given
            SellerAdmin original = SellerFixtures.activeSellerAdmin();

            // when
            SellerAdminJpaEntity entity = mapper.toEntity(original);
            SellerAdmin converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerIdValue()).isEqualTo(original.sellerIdValue());
            assertThat(converted.authUserId()).isEqualTo(original.authUserId());
            assertThat(converted.loginIdValue()).isEqualTo(original.loginIdValue());
            assertThat(converted.nameValue()).isEqualTo(original.nameValue());
            assertThat(converted.phoneNumberValue()).isEqualTo(original.phoneNumberValue());
            assertThat(converted.status()).isEqualTo(original.status());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SellerAdminJpaEntity original = SellerAdminJpaEntityFixtures.activeEntity();

            // when
            SellerAdmin domain = mapper.toDomain(original);
            SellerAdminJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getAuthUserId()).isEqualTo(original.getAuthUserId());
            assertThat(converted.getLoginId()).isEqualTo(original.getLoginId());
            assertThat(converted.getName()).isEqualTo(original.getName());
            assertThat(converted.getPhoneNumber()).isEqualTo(original.getPhoneNumber());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }
}
