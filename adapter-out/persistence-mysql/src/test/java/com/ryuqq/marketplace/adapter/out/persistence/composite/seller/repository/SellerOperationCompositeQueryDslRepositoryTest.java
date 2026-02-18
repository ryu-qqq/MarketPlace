package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition.SellerOperationConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.condition.SellerPolicyConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto.SellerOperationCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.RefundPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.RefundPolicyJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.SellerAddressJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleraddress.entity.SellerAddressJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.ShippingPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.ShippingPolicyJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * SellerOperationCompositeQueryDslRepositoryTest - 셀러 운영 메타데이터 Composite QueryDslRepository 통합 테스트.
 *
 * <p>주소(SellerAddress), 배송정책(ShippingPolicy), 환불정책(RefundPolicy)의 크로스 도메인 조회를 검증합니다.
 *
 * <p>soft-delete(deletedAt IS NULL) 필터 적용을 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("SellerOperationCompositeQueryDslRepository 통합 테스트")
class SellerOperationCompositeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerOperationCompositeQueryDslRepository repository() {
        return new SellerOperationCompositeQueryDslRepository(
                new JPAQueryFactory(entityManager),
                new SellerOperationConditionBuilder(),
                new SellerPolicyConditionBuilder());
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    // ========================================================================
    // 1. findBySellerId 기본 조회 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerId 기본 조회 테스트")
    class FindBySellerIdBasicTest {

        @Test
        @DisplayName("주소, 배송정책, 환불정책이 모두 있을 때 DTO를 반환합니다")
        void findBySellerId_WithAllData_ReturnsCompositeDto() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            SellerAddressJpaEntity shippingAddress =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(seller.getId());
            entityManager.persist(shippingAddress);

            SellerAddressJpaEntity returnAddress =
                    SellerAddressJpaEntityFixtures.defaultReturnEntity(seller.getId());
            entityManager.persist(returnAddress);

            ShippingPolicyJpaEntity shippingPolicy =
                    ShippingPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(shippingPolicy);

            RefundPolicyJpaEntity refundPolicy =
                    RefundPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(refundPolicy);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.sellerId()).isEqualTo(seller.getId());
            assertThat(result.addresses()).hasSize(2);
            assertThat(result.shippingPolicies()).hasSize(1);
            assertThat(result.refundPolicies()).hasSize(1);
        }

        @Test
        @DisplayName("데이터가 없는 셀러도 빈 리스트로 DTO를 반환합니다")
        void findBySellerId_WithNoData_ReturnsEmptyLists() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.sellerId()).isEqualTo(seller.getId());
            assertThat(result.addresses()).isEmpty();
            assertThat(result.shippingPolicies()).isEmpty();
            assertThat(result.refundPolicies()).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 셀러 ID로 조회하면 빈 리스트로 DTO를 반환합니다")
        void findBySellerId_WithNonExistentSellerId_ReturnsEmptyLists() {
            // given
            Long nonExistentSellerId = 9999L;

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(nonExistentSellerId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sellerId()).isEqualTo(nonExistentSellerId);
            assertThat(result.addresses()).isEmpty();
            assertThat(result.shippingPolicies()).isEmpty();
            assertThat(result.refundPolicies()).isEmpty();
        }
    }

    // ========================================================================
    // 2. 주소 Soft-delete 필터 테스트
    // ========================================================================

    @Nested
    @DisplayName("주소 Soft-delete 필터 테스트")
    class AddressSoftDeleteFilterTest {

        @Test
        @DisplayName("삭제된 주소는 조회되지 않습니다")
        void findBySellerId_WithDeletedAddress_FiltersDeleted() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            SellerAddressJpaEntity activeAddress =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(seller.getId());
            entityManager.persist(activeAddress);

            SellerAddressJpaEntity deletedAddress =
                    SellerAddressJpaEntityFixtures.deletedEntity(seller.getId());
            entityManager.persist(deletedAddress);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.addresses()).hasSize(1);
            assertThat(result.addresses().get(0).addressType())
                    .isEqualTo(SellerAddressJpaEntityFixtures.DEFAULT_ADDRESS_TYPE_SHIPPING);
            assertThat(result.addresses().get(0).defaultAddress()).isTrue();
        }

        @Test
        @DisplayName("모든 주소가 삭제된 경우 빈 리스트를 반환합니다")
        void findBySellerId_WithAllDeletedAddresses_ReturnsEmptyList() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            SellerAddressJpaEntity deleted1 =
                    SellerAddressJpaEntityFixtures.deletedEntity(seller.getId());
            entityManager.persist(deleted1);

            SellerAddressJpaEntity deleted2 =
                    SellerAddressJpaEntityFixtures.deletedEntity(seller.getId());
            entityManager.persist(deleted2);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.addresses()).isEmpty();
        }
    }

    // ========================================================================
    // 3. 배송정책 Soft-delete 필터 테스트
    // ========================================================================

    @Nested
    @DisplayName("배송정책 Soft-delete 필터 테스트")
    class ShippingPolicySoftDeleteFilterTest {

        @Test
        @DisplayName("삭제된 배송정책은 조회되지 않습니다")
        void findBySellerId_WithDeletedShippingPolicy_FiltersDeleted() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            ShippingPolicyJpaEntity active =
                    ShippingPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(active);

            ShippingPolicyJpaEntity deleted =
                    ShippingPolicyJpaEntityFixtures.newDeletedEntity(seller.getId());
            entityManager.persist(deleted);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.shippingPolicies()).hasSize(1);
            assertThat(result.shippingPolicies().get(0).defaultPolicy()).isTrue();
        }

        @Test
        @DisplayName("모든 배송정책이 삭제된 경우 빈 리스트를 반환합니다")
        void findBySellerId_WithAllDeletedShippingPolicies_ReturnsEmptyList() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            ShippingPolicyJpaEntity deleted1 =
                    ShippingPolicyJpaEntityFixtures.newDeletedEntity(seller.getId());
            entityManager.persist(deleted1);

            ShippingPolicyJpaEntity deleted2 =
                    ShippingPolicyJpaEntityFixtures.newDeletedEntity(seller.getId());
            entityManager.persist(deleted2);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.shippingPolicies()).isEmpty();
        }
    }

    // ========================================================================
    // 4. 환불정책 Soft-delete 필터 테스트
    // ========================================================================

    @Nested
    @DisplayName("환불정책 Soft-delete 필터 테스트")
    class RefundPolicySoftDeleteFilterTest {

        @Test
        @DisplayName("삭제된 환불정책은 조회되지 않습니다")
        void findBySellerId_WithDeletedRefundPolicy_FiltersDeleted() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            RefundPolicyJpaEntity active =
                    RefundPolicyJpaEntityFixtures.newActiveEntity(seller.getId());
            entityManager.persist(active);

            RefundPolicyJpaEntity deleted =
                    RefundPolicyJpaEntityFixtures.newDeletedEntity(seller.getId());
            entityManager.persist(deleted);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.refundPolicies()).hasSize(1);
            assertThat(result.refundPolicies().get(0).defaultPolicy()).isTrue();
        }

        @Test
        @DisplayName("모든 환불정책이 삭제된 경우 빈 리스트를 반환합니다")
        void findBySellerId_WithAllDeletedRefundPolicies_ReturnsEmptyList() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            RefundPolicyJpaEntity deleted1 =
                    RefundPolicyJpaEntityFixtures.newDeletedEntity(seller.getId());
            entityManager.persist(deleted1);

            RefundPolicyJpaEntity deleted2 =
                    RefundPolicyJpaEntityFixtures.newDeletedEntity(seller.getId());
            entityManager.persist(deleted2);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.refundPolicies()).isEmpty();
        }
    }

    // ========================================================================
    // 5. 주소 타입 및 기본 여부 데이터 검증 테스트
    // ========================================================================

    @Nested
    @DisplayName("주소 타입 및 기본 여부 데이터 검증 테스트")
    class AddressTypeAndDefaultTest {

        @Test
        @DisplayName("SHIPPING 타입 주소의 addressType이 올바르게 조회됩니다")
        void findBySellerId_WithShippingAddress_ReturnsCorrectAddressType() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            SellerAddressJpaEntity shipping =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(seller.getId());
            entityManager.persist(shipping);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.addresses()).hasSize(1);
            assertThat(result.addresses().get(0).addressType()).isEqualTo("SHIPPING");
            assertThat(result.addresses().get(0).defaultAddress()).isTrue();
        }

        @Test
        @DisplayName("RETURN 타입 주소의 addressType이 올바르게 조회됩니다")
        void findBySellerId_WithReturnAddress_ReturnsCorrectAddressType() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            SellerAddressJpaEntity returnAddr =
                    SellerAddressJpaEntityFixtures.defaultReturnEntity(seller.getId());
            entityManager.persist(returnAddr);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.addresses()).hasSize(1);
            assertThat(result.addresses().get(0).addressType()).isEqualTo("RETURN");
            assertThat(result.addresses().get(0).defaultAddress()).isTrue();
        }

        @Test
        @DisplayName("기본이 아닌 주소의 defaultAddress가 false로 조회됩니다")
        void findBySellerId_WithNonDefaultAddress_ReturnsFalseDefault() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            SellerAddressJpaEntity nonDefault =
                    SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(seller.getId(), "창고 B");
            entityManager.persist(nonDefault);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.addresses()).hasSize(1);
            assertThat(result.addresses().get(0).defaultAddress()).isFalse();
        }

        @Test
        @DisplayName("SHIPPING과 RETURN 타입 주소를 모두 조회합니다")
        void findBySellerId_WithBothAddressTypes_ReturnsBothTypes() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            SellerAddressJpaEntity shipping =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(seller.getId());
            entityManager.persist(shipping);

            SellerAddressJpaEntity returnAddr =
                    SellerAddressJpaEntityFixtures.defaultReturnEntity(seller.getId());
            entityManager.persist(returnAddr);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.addresses()).hasSize(2);
            long shippingCount =
                    result.addresses().stream()
                            .filter(a -> "SHIPPING".equals(a.addressType()))
                            .count();
            long returnCount =
                    result.addresses().stream()
                            .filter(a -> "RETURN".equals(a.addressType()))
                            .count();
            assertThat(shippingCount).isEqualTo(1L);
            assertThat(returnCount).isEqualTo(1L);
        }
    }

    // ========================================================================
    // 6. 다수 데이터 조회 테스트
    // ========================================================================

    @Nested
    @DisplayName("다수 데이터 조회 테스트")
    class MultipleDataTest {

        @Test
        @DisplayName("여러 주소를 모두 조회합니다")
        void findBySellerId_WithMultipleAddresses_ReturnsAll() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            SellerAddressJpaEntity shipping1 =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(seller.getId());
            entityManager.persist(shipping1);

            SellerAddressJpaEntity shipping2 =
                    SellerAddressJpaEntityFixtures.nonDefaultShippingEntity(seller.getId(), "창고 B");
            entityManager.persist(shipping2);

            SellerAddressJpaEntity returnAddr =
                    SellerAddressJpaEntityFixtures.defaultReturnEntity(seller.getId());
            entityManager.persist(returnAddr);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.addresses()).hasSize(3);
        }

        @Test
        @DisplayName("여러 배송정책을 모두 조회합니다")
        void findBySellerId_WithMultipleShippingPolicies_ReturnsAll() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            ShippingPolicyJpaEntity policy1 =
                    ShippingPolicyJpaEntityFixtures.newDefaultEntity(seller.getId());
            entityManager.persist(policy1);

            ShippingPolicyJpaEntity policy2 =
                    ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                            seller.getId(), "빠른 배송 정책");
            entityManager.persist(policy2);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.shippingPolicies()).hasSize(2);
        }

        @Test
        @DisplayName("여러 환불정책을 모두 조회합니다")
        void findBySellerId_WithMultipleRefundPolicies_ReturnsAll() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            RefundPolicyJpaEntity policy1 =
                    RefundPolicyJpaEntityFixtures.newDefaultEntity(seller.getId());
            entityManager.persist(policy1);

            RefundPolicyJpaEntity policy2 =
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(
                            seller.getId(), "프리미엄 환불 정책");
            entityManager.persist(policy2);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.refundPolicies()).hasSize(2);
        }

        @Test
        @DisplayName("다른 셀러의 데이터는 조회되지 않습니다")
        void findBySellerId_OtherSellerDataNotIncluded() {
            // given
            SellerJpaEntity seller1 = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller1);

            SellerJpaEntity seller2 = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller2);
            flushAndClear();

            SellerAddressJpaEntity address1 =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(seller1.getId());
            entityManager.persist(address1);

            SellerAddressJpaEntity address2 =
                    SellerAddressJpaEntityFixtures.defaultShippingEntity(seller2.getId());
            entityManager.persist(address2);

            ShippingPolicyJpaEntity policy1 =
                    ShippingPolicyJpaEntityFixtures.newActiveEntity(seller1.getId());
            entityManager.persist(policy1);

            ShippingPolicyJpaEntity policy2 =
                    ShippingPolicyJpaEntityFixtures.newActiveEntity(seller2.getId());
            entityManager.persist(policy2);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller1.getId());

            // then
            assertThat(result.sellerId()).isEqualTo(seller1.getId());
            assertThat(result.addresses()).hasSize(1);
            assertThat(result.shippingPolicies()).hasSize(1);
        }
    }

    // ========================================================================
    // 7. 정책 기본 여부 데이터 검증 테스트
    // ========================================================================

    @Nested
    @DisplayName("정책 기본 여부 데이터 검증 테스트")
    class PolicyDefaultTest {

        @Test
        @DisplayName("기본 배송정책의 defaultPolicy가 true로 조회됩니다")
        void findBySellerId_WithDefaultShippingPolicy_ReturnsTrueDefault() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            ShippingPolicyJpaEntity defaultPolicy =
                    ShippingPolicyJpaEntityFixtures.newDefaultEntity(seller.getId());
            entityManager.persist(defaultPolicy);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.shippingPolicies()).hasSize(1);
            assertThat(result.shippingPolicies().get(0).defaultPolicy()).isTrue();
        }

        @Test
        @DisplayName("기본이 아닌 배송정책의 defaultPolicy가 false로 조회됩니다")
        void findBySellerId_WithNonDefaultShippingPolicy_ReturnsFalseDefault() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            ShippingPolicyJpaEntity nonDefaultPolicy =
                    ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                            seller.getId(), "비기본 배송 정책");
            entityManager.persist(nonDefaultPolicy);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.shippingPolicies()).hasSize(1);
            assertThat(result.shippingPolicies().get(0).defaultPolicy()).isFalse();
        }

        @Test
        @DisplayName("기본 환불정책의 defaultPolicy가 true로 조회됩니다")
        void findBySellerId_WithDefaultRefundPolicy_ReturnsTrueDefault() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            RefundPolicyJpaEntity defaultPolicy =
                    RefundPolicyJpaEntityFixtures.newDefaultEntity(seller.getId());
            entityManager.persist(defaultPolicy);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.refundPolicies()).hasSize(1);
            assertThat(result.refundPolicies().get(0).defaultPolicy()).isTrue();
        }

        @Test
        @DisplayName("기본이 아닌 환불정책의 defaultPolicy가 false로 조회됩니다")
        void findBySellerId_WithNonDefaultRefundPolicy_ReturnsFalseDefault() {
            // given
            SellerJpaEntity seller = SellerJpaEntityFixtures.activeEntity();
            entityManager.persist(seller);
            flushAndClear();

            RefundPolicyJpaEntity nonDefaultPolicy =
                    RefundPolicyJpaEntityFixtures.newActiveEntityWithName(
                            seller.getId(), "비기본 환불 정책");
            entityManager.persist(nonDefaultPolicy);
            flushAndClear();

            // when
            SellerOperationCompositeDto result = repository().findBySellerId(seller.getId());

            // then
            assertThat(result.refundPolicies()).hasSize(1);
            assertThat(result.refundPolicies().get(0).defaultPolicy()).isFalse();
        }
    }
}
