package com.ryuqq.marketplace.domain.outboundproduct.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.outboundproduct.OutboundProductFixtures;
import com.ryuqq.marketplace.domain.outboundproduct.vo.OutboundProductStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OutboundProduct Aggregate 단위 테스트")
class OutboundProductTest {

    @Nested
    @DisplayName("forNew() - 신규 생성 (외부 상품 ID 없음)")
    class ForNewTest {

        @Test
        @DisplayName("신규 생성 시 PENDING_REGISTRATION 상태이다")
        void forNewCreatesWithPendingStatus() {
            OutboundProduct product = OutboundProductFixtures.newPendingProduct();

            assertThat(product.status()).isEqualTo(OutboundProductStatus.PENDING_REGISTRATION);
            assertThat(product.externalProductId()).isNull();
        }

        @Test
        @DisplayName("신규 생성 시 ID는 null이다 (isNew=true)")
        void forNewHasNullId() {
            OutboundProduct product = OutboundProductFixtures.newPendingProduct();

            assertThat(product.isNew()).isTrue();
            assertThat(product.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("신규 생성 시 shopId, productGroupId, salesChannelId가 설정된다")
        void forNewSetsCorrectFields() {
            OutboundProduct product = OutboundProductFixtures.newPendingProduct();

            assertThat(product.shopId()).isEqualTo(OutboundProductFixtures.DEFAULT_SHOP_ID);
            assertThat(product.productGroupIdValue())
                    .isEqualTo(OutboundProductFixtures.DEFAULT_PRODUCT_GROUP_ID);
            assertThat(product.salesChannelIdValue())
                    .isEqualTo(OutboundProductFixtures.DEFAULT_SALES_CHANNEL_ID);
        }
    }

    @Nested
    @DisplayName("forNewWithExternalId() - 외부 상품 ID 포함 신규 생성")
    class ForNewWithExternalIdTest {

        @Test
        @DisplayName("외부 상품 ID 포함 생성 시 REGISTERED 상태이다")
        void forNewWithExternalIdCreatesRegistered() {
            OutboundProduct product = OutboundProductFixtures.newRegisteredProduct();

            assertThat(product.status()).isEqualTo(OutboundProductStatus.REGISTERED);
            assertThat(product.externalProductId())
                    .isEqualTo(OutboundProductFixtures.DEFAULT_EXTERNAL_PRODUCT_ID);
        }

        @Test
        @DisplayName("외부 상품 ID 포함 생성 시 isRegistered()가 true이다")
        void forNewWithExternalIdIsRegistered() {
            OutboundProduct product = OutboundProductFixtures.newRegisteredProduct();

            assertThat(product.isRegistered()).isTrue();
        }
    }

    @Nested
    @DisplayName("registerExternalProduct() - 외부 상품 ID 등록")
    class RegisterExternalProductTest {

        @Test
        @DisplayName("PENDING_REGISTRATION 상태에서 외부 상품을 등록한다")
        void registerExternalProductFromPending() {
            OutboundProduct product = OutboundProductFixtures.pendingProduct();
            Instant now = CommonVoFixtures.now();

            product.registerExternalProduct("EXT-NEW-001", now);

            assertThat(product.status()).isEqualTo(OutboundProductStatus.REGISTERED);
            assertThat(product.externalProductId()).isEqualTo("EXT-NEW-001");
            assertThat(product.isRegistered()).isTrue();
        }

        @Test
        @DisplayName("REGISTERED 상태에서 외부 상품 등록하면 예외가 발생한다")
        void registerExternalProductFromRegisteredThrowsException() {
            OutboundProduct product = OutboundProductFixtures.registeredProduct();

            assertThatThrownBy(
                            () ->
                                    product.registerExternalProduct(
                                            "EXT-NEW", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDING_REGISTRATION");
        }

        @Test
        @DisplayName("REGISTRATION_FAILED 상태에서 외부 상품 등록하면 예외가 발생한다")
        void registerExternalProductFromFailedThrowsException() {
            OutboundProduct product = OutboundProductFixtures.failedProduct();

            assertThatThrownBy(
                            () ->
                                    product.registerExternalProduct(
                                            "EXT-NEW", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDING_REGISTRATION");
        }
    }

    @Nested
    @DisplayName("markRegistrationFailed() - 등록 실패 처리")
    class MarkRegistrationFailedTest {

        @Test
        @DisplayName("PENDING_REGISTRATION 상태에서 실패 처리한다")
        void markFailedFromPending() {
            OutboundProduct product = OutboundProductFixtures.pendingProduct();

            product.markRegistrationFailed(CommonVoFixtures.now());

            assertThat(product.status()).isEqualTo(OutboundProductStatus.REGISTRATION_FAILED);
        }

        @Test
        @DisplayName("REGISTERED 상태에서 실패 처리하면 예외가 발생한다")
        void markFailedFromRegisteredThrowsException() {
            OutboundProduct product = OutboundProductFixtures.registeredProduct();

            assertThatThrownBy(() -> product.markRegistrationFailed(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDING_REGISTRATION");
        }
    }

    @Nested
    @DisplayName("retryRegistration() - 재시도")
    class RetryRegistrationTest {

        @Test
        @DisplayName("REGISTRATION_FAILED 상태에서 재시도하면 PENDING_REGISTRATION으로 전환한다")
        void retryFromFailed() {
            OutboundProduct product = OutboundProductFixtures.failedProduct();

            product.retryRegistration(CommonVoFixtures.now());

            assertThat(product.status()).isEqualTo(OutboundProductStatus.PENDING_REGISTRATION);
        }

        @Test
        @DisplayName("PENDING_REGISTRATION 상태에서 재시도하면 예외가 발생한다")
        void retryFromPendingThrowsException() {
            OutboundProduct product = OutboundProductFixtures.pendingProduct();

            assertThatThrownBy(() -> product.retryRegistration(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("REGISTRATION_FAILED");
        }

        @Test
        @DisplayName("REGISTERED 상태에서 재시도하면 예외가 발생한다")
        void retryFromRegisteredThrowsException() {
            OutboundProduct product = OutboundProductFixtures.registeredProduct();

            assertThatThrownBy(() -> product.retryRegistration(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("REGISTRATION_FAILED");
        }
    }

    @Nested
    @DisplayName("deregister() - 등록 해제")
    class DeregisterTest {

        @Test
        @DisplayName("REGISTERED 상태에서 등록 해제한다")
        void deregisterFromRegistered() {
            OutboundProduct product = OutboundProductFixtures.registeredProduct();

            product.deregister(CommonVoFixtures.now());

            assertThat(product.status()).isEqualTo(OutboundProductStatus.DEREGISTERED);
            assertThat(product.isDeregistered()).isTrue();
        }

        @Test
        @DisplayName("등록 해제 후 externalProductId는 보존된다")
        void externalProductIdPreservedAfterDeregister() {
            OutboundProduct product = OutboundProductFixtures.registeredProduct();

            product.deregister(CommonVoFixtures.now());

            assertThat(product.externalProductId())
                    .isEqualTo(OutboundProductFixtures.DEFAULT_EXTERNAL_PRODUCT_ID);
        }

        @Test
        @DisplayName("PENDING_REGISTRATION 상태에서 등록 해제하면 예외가 발생한다")
        void deregisterFromPendingThrowsException() {
            OutboundProduct product = OutboundProductFixtures.pendingProduct();

            assertThatThrownBy(() -> product.deregister(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("REGISTERED");
        }
    }

    @Nested
    @DisplayName("prepareReregistration() - 재등록 준비")
    class PrepareReregistrationTest {

        @Test
        @DisplayName("DEREGISTERED 상태에서 재등록 준비하면 PENDING_REGISTRATION으로 전환한다")
        void prepareReregistrationFromDeregistered() {
            OutboundProduct product = OutboundProductFixtures.registeredProduct();
            product.deregister(CommonVoFixtures.now());

            product.prepareReregistration(CommonVoFixtures.now());

            assertThat(product.status()).isEqualTo(OutboundProductStatus.PENDING_REGISTRATION);
        }

        @Test
        @DisplayName("REGISTERED 상태에서 재등록 준비하면 예외가 발생한다")
        void prepareReregistrationFromRegisteredThrowsException() {
            OutboundProduct product = OutboundProductFixtures.registeredProduct();

            assertThatThrownBy(() -> product.prepareReregistration(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("DEREGISTERED");
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("모든 필드를 복원한다")
        void reconstituteWithAllFields() {
            OutboundProduct product = OutboundProductFixtures.registeredProduct();

            assertThat(product.idValue()).isEqualTo(OutboundProductFixtures.DEFAULT_ID);
            assertThat(product.status()).isEqualTo(OutboundProductStatus.REGISTERED);
            assertThat(product.externalProductId())
                    .isEqualTo(OutboundProductFixtures.DEFAULT_EXTERNAL_PRODUCT_ID);
            assertThat(product.isNew()).isFalse();
        }
    }

    @Nested
    @DisplayName("상태 전이 플로우 테스트")
    class StateTransitionFlowTest {

        @Test
        @DisplayName("PENDING → 실패 → 재시도 → 등록 완전 플로우")
        void pendingToFailedToRetryToRegistered() {
            OutboundProduct product = OutboundProductFixtures.pendingProduct();

            product.markRegistrationFailed(CommonVoFixtures.now());
            assertThat(product.status()).isEqualTo(OutboundProductStatus.REGISTRATION_FAILED);

            product.retryRegistration(CommonVoFixtures.now());
            assertThat(product.status()).isEqualTo(OutboundProductStatus.PENDING_REGISTRATION);

            product.registerExternalProduct("EXT-001", CommonVoFixtures.now());
            assertThat(product.status()).isEqualTo(OutboundProductStatus.REGISTERED);
            assertThat(product.isRegistered()).isTrue();
        }

        @Test
        @DisplayName("REGISTERED → DEREGISTERED → PENDING_REGISTRATION 재등록 플로우")
        void registeredToDeregisteredToPending() {
            OutboundProduct product = OutboundProductFixtures.registeredProduct();

            product.deregister(CommonVoFixtures.now());
            assertThat(product.status()).isEqualTo(OutboundProductStatus.DEREGISTERED);

            product.prepareReregistration(CommonVoFixtures.now());
            assertThat(product.status()).isEqualTo(OutboundProductStatus.PENDING_REGISTRATION);
        }
    }
}
