package com.ryuqq.marketplace.domain.claimhistory.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ActorType enum 단위 테스트")
class ActorTypeTest {

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 ActorType 값이 존재한다")
        void allValuesExist() {
            // then
            assertThat(ActorType.values())
                    .containsExactly(
                            ActorType.CUSTOMER,
                            ActorType.SELLER,
                            ActorType.ADMIN,
                            ActorType.SYSTEM);
        }

        @Test
        @DisplayName("CUSTOMER의 description은 '고객'이다")
        void customerDescription() {
            // then
            assertThat(ActorType.CUSTOMER.description()).isEqualTo("고객");
        }

        @Test
        @DisplayName("SELLER의 description은 '판매자'이다")
        void sellerDescription() {
            // then
            assertThat(ActorType.SELLER.description()).isEqualTo("판매자");
        }

        @Test
        @DisplayName("ADMIN의 description은 '관리자'이다")
        void adminDescription() {
            // then
            assertThat(ActorType.ADMIN.description()).isEqualTo("관리자");
        }

        @Test
        @DisplayName("SYSTEM의 description은 '시스템'이다")
        void systemDescription() {
            // then
            assertThat(ActorType.SYSTEM.description()).isEqualTo("시스템");
        }
    }

    @Nested
    @DisplayName("enum 기본 동작 테스트")
    class EnumBehaviorTest {

        @Test
        @DisplayName("name()으로 enum 이름을 반환한다")
        void nameReturnsEnumName() {
            // then
            assertThat(ActorType.CUSTOMER.name()).isEqualTo("CUSTOMER");
            assertThat(ActorType.SELLER.name()).isEqualTo("SELLER");
            assertThat(ActorType.ADMIN.name()).isEqualTo("ADMIN");
            assertThat(ActorType.SYSTEM.name()).isEqualTo("SYSTEM");
        }

        @Test
        @DisplayName("valueOf()로 enum 값을 조회한다")
        void valueOfReturnsEnum() {
            // then
            assertThat(ActorType.valueOf("CUSTOMER")).isEqualTo(ActorType.CUSTOMER);
            assertThat(ActorType.valueOf("SELLER")).isEqualTo(ActorType.SELLER);
            assertThat(ActorType.valueOf("ADMIN")).isEqualTo(ActorType.ADMIN);
            assertThat(ActorType.valueOf("SYSTEM")).isEqualTo(ActorType.SYSTEM);
        }
    }
}
