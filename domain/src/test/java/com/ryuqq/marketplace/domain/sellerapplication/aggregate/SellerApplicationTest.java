package com.ryuqq.marketplace.domain.sellerapplication.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.event.DomainEvent;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellerapplication.SellerApplicationFixtures;
import com.ryuqq.marketplace.domain.sellerapplication.event.SellerApplicationAppliedEvent;
import com.ryuqq.marketplace.domain.sellerapplication.event.SellerApplicationApprovedEvent;
import com.ryuqq.marketplace.domain.sellerapplication.event.SellerApplicationRejectedEvent;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerApplication Aggregate 테스트")
class SellerApplicationTest {

    @Nested
    @DisplayName("apply() - 입점 신청 생성")
    class ApplyTest {

        @Test
        @DisplayName("신규 입점 신청을 생성한다")
        void createNewApplication() {
            // when
            SellerApplication application = SellerApplicationFixtures.newApplication();

            // then
            assertThat(application.isNew()).isTrue();
            assertThat(application.status()).isEqualTo(ApplicationStatus.PENDING);
            assertThat(application.isPending()).isTrue();
            assertThat(application.isApproved()).isFalse();
            assertThat(application.isRejected()).isFalse();
            assertThat(application.processedAt()).isNull();
            assertThat(application.processedBy()).isNull();
            assertThat(application.rejectionReason()).isNull();
            assertThat(application.approvedSellerId()).isNull();
        }

        @Test
        @DisplayName("신규 신청 시 SellerApplicationAppliedEvent가 등록된다")
        void appliedEventRegistered() {
            // when
            SellerApplication application = SellerApplicationFixtures.newApplication();

            // then
            List<DomainEvent> events = application.pollEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(SellerApplicationAppliedEvent.class);

            SellerApplicationAppliedEvent event = (SellerApplicationAppliedEvent) events.get(0);
            assertThat(event.applicationId()).isEqualTo(application.id());
            assertThat(event.sellerName()).isEqualTo(application.sellerNameValue());
            assertThat(event.registrationNumber()).isEqualTo(application.registrationNumberValue());
        }

        @Test
        @DisplayName("신규 신청 시 기본 정보가 올바르게 설정된다")
        void applicationFieldsSetCorrectly() {
            // when
            SellerApplication application = SellerApplicationFixtures.newApplication();

            // then
            assertThat(application.sellerNameValue()).isEqualTo("테스트 셀러");
            assertThat(application.displayNameValue()).isEqualTo("테스트 셀러 스토어");
            assertThat(application.registrationNumberValue()).isEqualTo("123-45-67890");
            assertThat(application.companyNameValue()).isEqualTo("테스트 주식회사");
            assertThat(application.representativeValue()).isEqualTo("홍길동");
        }
    }

    @Nested
    @DisplayName("approve() - 입점 승인")
    class ApproveTest {

        @Test
        @DisplayName("대기 중인 신청을 승인한다")
        void approvePendingApplication() {
            // given
            SellerApplication application = SellerApplicationFixtures.pendingApplication();
            SellerId approvedSellerId = SellerId.of(100L);
            String processedBy = "admin@marketplace.com";
            Instant now = CommonVoFixtures.now();

            // when
            application.approve(approvedSellerId, processedBy, now);

            // then
            assertThat(application.isApproved()).isTrue();
            assertThat(application.isPending()).isFalse();
            assertThat(application.status()).isEqualTo(ApplicationStatus.APPROVED);
            assertThat(application.approvedSellerId()).isEqualTo(approvedSellerId);
            assertThat(application.approvedSellerIdValue()).isEqualTo(100L);
            assertThat(application.processedBy()).isEqualTo(processedBy);
            assertThat(application.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("승인 시 SellerApplicationApprovedEvent가 등록된다")
        void approvedEventRegistered() {
            // given
            SellerApplication application = SellerApplicationFixtures.pendingApplication();
            SellerId approvedSellerId = SellerId.of(100L);
            Instant now = CommonVoFixtures.now();

            // when
            application.approve(approvedSellerId, "admin@marketplace.com", now);

            // then
            List<DomainEvent> events = application.pollEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(SellerApplicationApprovedEvent.class);

            SellerApplicationApprovedEvent event = (SellerApplicationApprovedEvent) events.get(0);
            assertThat(event.approvedSellerId()).isEqualTo(approvedSellerId);
            assertThat(event.processedBy()).isEqualTo("admin@marketplace.com");
        }

        @Test
        @DisplayName("이미 승인된 신청을 다시 승인하면 예외가 발생한다")
        void approveAlreadyApproved_ThrowsException() {
            // given
            SellerApplication application = SellerApplicationFixtures.approvedApplication();

            // when & then
            assertThatThrownBy(
                            () ->
                                    application.approve(
                                            SellerId.of(200L),
                                            "admin@marketplace.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("이미 처리된 신청");
        }

        @Test
        @DisplayName("이미 거절된 신청을 승인하면 예외가 발생한다")
        void approveRejectedApplication_ThrowsException() {
            // given
            SellerApplication application = SellerApplicationFixtures.rejectedApplication();

            // when & then
            assertThatThrownBy(
                            () ->
                                    application.approve(
                                            SellerId.of(200L),
                                            "admin@marketplace.com",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("이미 처리된 신청");
        }
    }

    @Nested
    @DisplayName("reject() - 입점 거절")
    class RejectTest {

        @Test
        @DisplayName("대기 중인 신청을 거절한다")
        void rejectPendingApplication() {
            // given
            SellerApplication application = SellerApplicationFixtures.pendingApplication();
            String reason = "서류 미비";
            String processedBy = "admin@marketplace.com";
            Instant now = CommonVoFixtures.now();

            // when
            application.reject(reason, processedBy, now);

            // then
            assertThat(application.isRejected()).isTrue();
            assertThat(application.isPending()).isFalse();
            assertThat(application.status()).isEqualTo(ApplicationStatus.REJECTED);
            assertThat(application.rejectionReason()).isEqualTo(reason);
            assertThat(application.processedBy()).isEqualTo(processedBy);
            assertThat(application.processedAt()).isEqualTo(now);
            assertThat(application.approvedSellerId()).isNull();
        }

        @Test
        @DisplayName("거절 시 SellerApplicationRejectedEvent가 등록된다")
        void rejectedEventRegistered() {
            // given
            SellerApplication application = SellerApplicationFixtures.pendingApplication();
            Instant now = CommonVoFixtures.now();

            // when
            application.reject("서류 미비", "admin@marketplace.com", now);

            // then
            List<DomainEvent> events = application.pollEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(SellerApplicationRejectedEvent.class);

            SellerApplicationRejectedEvent event = (SellerApplicationRejectedEvent) events.get(0);
            assertThat(event.rejectionReason()).isEqualTo("서류 미비");
            assertThat(event.processedBy()).isEqualTo("admin@marketplace.com");
        }

        @Test
        @DisplayName("거절 사유가 null이면 예외가 발생한다")
        void rejectWithNullReason_ThrowsException() {
            // given
            SellerApplication application = SellerApplicationFixtures.pendingApplication();

            // when & then
            assertThatThrownBy(
                            () ->
                                    application.reject(
                                            null, "admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("거절 사유는 필수");
        }

        @Test
        @DisplayName("거절 사유가 빈 문자열이면 예외가 발생한다")
        void rejectWithBlankReason_ThrowsException() {
            // given
            SellerApplication application = SellerApplicationFixtures.pendingApplication();

            // when & then
            assertThatThrownBy(
                            () ->
                                    application.reject(
                                            "   ", "admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("거절 사유는 필수");
        }

        @Test
        @DisplayName("이미 승인된 신청을 거절하면 예외가 발생한다")
        void rejectApprovedApplication_ThrowsException() {
            // given
            SellerApplication application = SellerApplicationFixtures.approvedApplication();

            // when & then
            assertThatThrownBy(
                            () ->
                                    application.reject(
                                            "사유", "admin@marketplace.com", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("이미 처리된 신청");
        }
    }

    @Nested
    @DisplayName("pollEvents() - 이벤트 수집")
    class PollEventsTest {

        @Test
        @DisplayName("pollEvents 호출 후 이벤트가 비워진다")
        void eventsAreClearedAfterPoll() {
            // given
            SellerApplication application = SellerApplicationFixtures.newApplication();

            // when
            List<DomainEvent> firstPoll = application.pollEvents();
            List<DomainEvent> secondPoll = application.pollEvents();

            // then
            assertThat(firstPoll).hasSize(1);
            assertThat(secondPoll).isEmpty();
        }

        @Test
        @DisplayName("pollEvents 결과는 불변 리스트이다")
        void pollEventsReturnsUnmodifiableList() {
            // given
            SellerApplication application = SellerApplicationFixtures.newApplication();

            // when
            List<DomainEvent> events = application.pollEvents();

            // then
            assertThatThrownBy(() -> events.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("대기 상태로 재구성한다")
        void reconstitutePendingApplication() {
            // when
            SellerApplication application = SellerApplicationFixtures.pendingApplication(1L);

            // then
            assertThat(application.isNew()).isFalse();
            assertThat(application.idValue()).isEqualTo(1L);
            assertThat(application.isPending()).isTrue();
            assertThat(application.processedAt()).isNull();
            assertThat(application.approvedSellerId()).isNull();
        }

        @Test
        @DisplayName("승인 상태로 재구성한다")
        void reconstituteApprovedApplication() {
            // when
            SellerApplication application = SellerApplicationFixtures.approvedApplication(2L);

            // then
            assertThat(application.idValue()).isEqualTo(2L);
            assertThat(application.isApproved()).isTrue();
            assertThat(application.approvedSellerIdValue()).isEqualTo(100L);
            assertThat(application.processedBy()).isNotNull();
            assertThat(application.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("거절 상태로 재구성한다")
        void reconstituteRejectedApplication() {
            // when
            SellerApplication application = SellerApplicationFixtures.rejectedApplication(3L);

            // then
            assertThat(application.idValue()).isEqualTo(3L);
            assertThat(application.isRejected()).isTrue();
            assertThat(application.rejectionReason()).isNotNull();
            assertThat(application.processedBy()).isNotNull();
            assertThat(application.approvedSellerId()).isNull();
        }
    }

    @Nested
    @DisplayName("VO getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("nullable 필드가 null일 때 안전하게 반환한다")
        void nullableFieldsSafeReturn() {
            // given
            SellerApplication application = SellerApplicationFixtures.pendingApplication();

            // then
            assertThat(application.approvedSellerIdValue()).isNull();
            assertThat(application.processedAt()).isNull();
            assertThat(application.processedBy()).isNull();
            assertThat(application.rejectionReason()).isNull();
        }

        @Test
        @DisplayName("정산 정보를 올바르게 반환한다")
        void settlementInfoReturned() {
            // given
            SellerApplication application = SellerApplicationFixtures.pendingApplication();

            // then
            assertThat(application.bankAccount()).isNotNull();
            assertThat(application.settlementCycle()).isNotNull();
            assertThat(application.settlementDay()).isEqualTo(15);
        }
    }
}
