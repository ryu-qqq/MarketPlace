package com.ryuqq.marketplace.domain.settlement.entry.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("EntrySourceReference Value Object 단위 테스트")
class EntrySourceReferenceTest {

    @Nested
    @DisplayName("forSales() - 판매 Entry 참조 생성")
    class ForSalesTest {

        @Test
        @DisplayName("판매 Entry 참조를 생성한다")
        void createForSalesReference() {
            String orderItemId = "oi-test-001";

            EntrySourceReference reference = EntrySourceReference.forSales(orderItemId);

            assertThat(reference.orderItemId()).isEqualTo(orderItemId);
            assertThat(reference.claimId()).isNull();
            assertThat(reference.claimType()).isNull();
        }

        @Test
        @DisplayName("orderItemId가 null이면 예외가 발생한다")
        void throwWhenOrderItemIdIsNull() {
            assertThatThrownBy(() -> EntrySourceReference.forSales(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("orderItemId");
        }

        @Test
        @DisplayName("orderItemId가 빈 문자열이면 예외가 발생한다")
        void throwWhenOrderItemIdIsBlank() {
            assertThatThrownBy(() -> EntrySourceReference.forSales(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("orderItemId");
        }
    }

    @Nested
    @DisplayName("forClaim() - 클레임 역분개 Entry 참조 생성")
    class ForClaimTest {

        @Test
        @DisplayName("취소 클레임 참조를 생성한다")
        void createForCancelClaimReference() {
            String orderItemId = "oi-test-001";
            String claimId = "cancel-001";
            String claimType = "CANCEL";

            EntrySourceReference reference =
                    EntrySourceReference.forClaim(orderItemId, claimId, claimType);

            assertThat(reference.orderItemId()).isEqualTo(orderItemId);
            assertThat(reference.claimId()).isEqualTo(claimId);
            assertThat(reference.claimType()).isEqualTo(claimType);
        }

        @Test
        @DisplayName("환불 클레임 참조를 생성한다")
        void createForRefundClaimReference() {
            EntrySourceReference reference =
                    EntrySourceReference.forClaim("oi-002", "refund-001", "REFUND");

            assertThat(reference.claimType()).isEqualTo("REFUND");
        }

        @Test
        @DisplayName("claimId와 claimType이 null이어도 생성된다")
        void createWithNullClaimInfo() {
            EntrySourceReference reference = EntrySourceReference.forClaim("oi-001", null, null);

            assertThat(reference.orderItemId()).isEqualTo("oi-001");
            assertThat(reference.claimId()).isNull();
            assertThat(reference.claimType()).isNull();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            EntrySourceReference ref1 = EntrySourceReference.forSales("oi-001");
            EntrySourceReference ref2 = EntrySourceReference.forSales("oi-001");

            assertThat(ref1).isEqualTo(ref2);
            assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
        }

        @Test
        @DisplayName("orderItemId가 다르면 다르다")
        void differentOrderItemIdAreNotEqual() {
            EntrySourceReference ref1 = EntrySourceReference.forSales("oi-001");
            EntrySourceReference ref2 = EntrySourceReference.forSales("oi-002");

            assertThat(ref1).isNotEqualTo(ref2);
        }

        @Test
        @DisplayName("클레임 정보 유무가 다르면 다르다")
        void differentClaimInfoAreNotEqual() {
            EntrySourceReference salesRef = EntrySourceReference.forSales("oi-001");
            EntrySourceReference claimRef =
                    EntrySourceReference.forClaim("oi-001", "cancel-001", "CANCEL");

            assertThat(salesRef).isNotEqualTo(claimRef);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("record이므로 필드값을 직접 변경할 수 없다")
        void recordIsImmutable() {
            EntrySourceReference reference = EntrySourceReference.forSales("oi-001");

            assertThat(reference.orderItemId()).isEqualTo("oi-001");
            assertThat(reference.claimId()).isNull();
        }
    }
}
