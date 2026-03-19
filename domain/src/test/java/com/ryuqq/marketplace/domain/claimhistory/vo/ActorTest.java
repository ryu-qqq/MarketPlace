package com.ryuqq.marketplace.domain.claimhistory.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("Actor Value Object 단위 테스트")
class ActorTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("system()으로 시스템 Actor를 생성한다")
        void createSystemActor() {
            // when
            Actor actor = Actor.system();

            // then
            assertThat(actor.actorType()).isEqualTo(ActorType.SYSTEM);
            assertThat(actor.actorId()).isEqualTo("system");
            assertThat(actor.actorName()).isEqualTo("시스템");
        }

        @Test
        @DisplayName("admin()으로 관리자 Actor를 생성한다")
        void createAdminActor() {
            // when
            Actor actor = Actor.admin("admin-001", "관리자");

            // then
            assertThat(actor.actorType()).isEqualTo(ActorType.ADMIN);
            assertThat(actor.actorId()).isEqualTo("admin-001");
            assertThat(actor.actorName()).isEqualTo("관리자");
        }

        @Test
        @DisplayName("seller()로 판매자 Actor를 생성한다")
        void createSellerActor() {
            // when
            Actor actor = Actor.seller("seller-001", "판매자");

            // then
            assertThat(actor.actorType()).isEqualTo(ActorType.SELLER);
            assertThat(actor.actorId()).isEqualTo("seller-001");
            assertThat(actor.actorName()).isEqualTo("판매자");
        }

        @Test
        @DisplayName("customer()로 고객 Actor를 생성한다")
        void createCustomerActor() {
            // when
            Actor actor = Actor.customer("customer-001", "고객");

            // then
            assertThat(actor.actorType()).isEqualTo(ActorType.CUSTOMER);
            assertThat(actor.actorId()).isEqualTo("customer-001");
            assertThat(actor.actorName()).isEqualTo("고객");
        }
    }

    @Nested
    @DisplayName("유효성 검증 테스트")
    class ValidationTest {

        @Test
        @DisplayName("actorType이 null이면 예외가 발생한다")
        void nullActorTypeThrowsException() {
            // when & then
            assertThatThrownBy(() -> new Actor(null, "actor-001", "이름"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("actorType");
        }

        @Test
        @DisplayName("actorId가 null이면 예외가 발생한다")
        void nullActorIdThrowsException() {
            // when & then
            assertThatThrownBy(() -> new Actor(ActorType.ADMIN, null, "이름"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("actorId");
        }

        @Test
        @DisplayName("actorId가 빈 문자열이면 예외가 발생한다")
        void blankActorIdThrowsException() {
            // when & then
            assertThatThrownBy(() -> new Actor(ActorType.ADMIN, "", "이름"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("actorId");
        }

        @Test
        @DisplayName("actorId가 공백만 있으면 예외가 발생한다")
        void whitespaceActorIdThrowsException() {
            // when & then
            assertThatThrownBy(() -> new Actor(ActorType.SELLER, "   ", "이름"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("actorId");
        }

        @Test
        @DisplayName("actorName이 null이어도 생성에 성공한다")
        void nullActorNameIsAllowed() {
            // when
            Actor actor = new Actor(ActorType.SYSTEM, "system", null);

            // then
            assertThat(actor.actorType()).isEqualTo(ActorType.SYSTEM);
            assertThat(actor.actorId()).isEqualTo("system");
            assertThat(actor.actorName()).isNull();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값의 Actor는 동등하다")
        void sameValueActorsAreEqual() {
            // given
            Actor actor1 = Actor.admin("admin-001", "관리자");
            Actor actor2 = Actor.admin("admin-001", "관리자");

            // then
            assertThat(actor1).isEqualTo(actor2);
            assertThat(actor1.hashCode()).isEqualTo(actor2.hashCode());
        }

        @Test
        @DisplayName("다른 actorId의 Actor는 동등하지 않다")
        void differentActorIdActorsAreNotEqual() {
            // given
            Actor actor1 = Actor.admin("admin-001", "관리자1");
            Actor actor2 = Actor.admin("admin-002", "관리자2");

            // then
            assertThat(actor1).isNotEqualTo(actor2);
        }

        @Test
        @DisplayName("다른 ActorType의 Actor는 동등하지 않다")
        void differentActorTypeActorsAreNotEqual() {
            // given
            Actor actor1 = Actor.admin("user-001", "사용자");
            Actor actor2 = Actor.seller("user-001", "사용자");

            // then
            assertThat(actor1).isNotEqualTo(actor2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("Actor는 record이므로 불변이다")
        void actorIsImmutable() {
            // given
            Actor actor = Actor.admin("admin-001", "관리자");

            // then
            assertThat(actor.actorType()).isEqualTo(ActorType.ADMIN);
            assertThat(actor.actorId()).isEqualTo("admin-001");
            assertThat(actor.actorName()).isEqualTo("관리자");
        }
    }
}
