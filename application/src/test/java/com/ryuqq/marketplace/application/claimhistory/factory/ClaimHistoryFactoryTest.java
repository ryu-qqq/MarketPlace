package com.ryuqq.marketplace.application.claimhistory.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ActorType;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimHistoryType;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ClaimHistoryFactory 단위 테스트")
class ClaimHistoryFactoryTest {

    @InjectMocks private ClaimHistoryFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createManualMemo() - 수기 메모 이력 생성")
    class CreateManualMemoTest {

        @Test
        @DisplayName("수기 메모 이력을 생성하고 MANUAL 타입으로 반환한다")
        void createManualMemo_ValidParams_ReturnsManualClaimHistory() {
            // given
            ClaimType claimType = ClaimType.CANCEL;
            String claimId = "cancel-claim-001";
            String message = "고객 요청으로 취소 처리 확인";
            String actorId = "admin-001";
            String actorName = "관리자";
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            ClaimHistory result =
                    sut.createManualMemo(claimType, claimId, message, actorId, actorName);

            // then
            assertThat(result).isNotNull();
            assertThat(result.idValue()).isNotNull();
            assertThat(result.claimType()).isEqualTo(claimType);
            assertThat(result.claimId()).isEqualTo(claimId);
            assertThat(result.historyType()).isEqualTo(ClaimHistoryType.MANUAL);
            assertThat(result.title()).isEqualTo("CS 메모");
            assertThat(result.message()).isEqualTo(message);
            assertThat(result.actor().actorType()).isEqualTo(ActorType.ADMIN);
            assertThat(result.actor().actorId()).isEqualTo(actorId);
            assertThat(result.actor().actorName()).isEqualTo(actorName);
            assertThat(result.createdAt()).isEqualTo(now);
            then(timeProvider).should().now();
        }

        @Test
        @DisplayName("환불 클레임에 대한 수기 메모 이력을 생성한다")
        void createManualMemo_RefundClaimType_ReturnsManualClaimHistory() {
            // given
            ClaimType claimType = ClaimType.REFUND;
            String claimId = "refund-claim-001";
            String message = "환불 금액 확인 완료";
            String actorId = "admin-002";
            String actorName = "CS 담당자";
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            ClaimHistory result =
                    sut.createManualMemo(claimType, claimId, message, actorId, actorName);

            // then
            assertThat(result.claimType()).isEqualTo(ClaimType.REFUND);
            assertThat(result.historyType()).isEqualTo(ClaimHistoryType.MANUAL);
            assertThat(result.message()).isEqualTo(message);
        }
    }

    @Nested
    @DisplayName("createStatusChange() - 상태 변경 이력 생성")
    class CreateStatusChangeTest {

        @Test
        @DisplayName("상태 변경 이력을 생성하고 STATUS_CHANGE 타입으로 반환한다")
        void createStatusChange_ValidParams_ReturnsStatusChangeClaimHistory() {
            // given
            ClaimType claimType = ClaimType.CANCEL;
            String claimId = "cancel-claim-001";
            String fromStatus = "REQUESTED";
            String toStatus = "APPROVED";
            String actorId = "admin-001";
            String actorName = "관리자";
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            ClaimHistory result =
                    sut.createStatusChange(
                            claimType, claimId, fromStatus, toStatus, actorId, actorName);

            // then
            assertThat(result).isNotNull();
            assertThat(result.claimType()).isEqualTo(claimType);
            assertThat(result.claimId()).isEqualTo(claimId);
            assertThat(result.historyType()).isEqualTo(ClaimHistoryType.STATUS_CHANGE);
            assertThat(result.title()).isEqualTo("승인");
            assertThat(result.message()).isEqualTo("REQUESTED → APPROVED");
            assertThat(result.actor().actorType()).isEqualTo(ActorType.ADMIN);
            assertThat(result.createdAt()).isEqualTo(now);
            then(timeProvider).should().now();
        }

        @Test
        @DisplayName("교환 클레임의 상태 변경 이력을 생성한다")
        void createStatusChange_ExchangeClaimType_ReturnsStatusChangeClaimHistory() {
            // given
            ClaimType claimType = ClaimType.EXCHANGE;
            String claimId = "exchange-claim-001";
            String fromStatus = "COLLECTING";
            String toStatus = "COMPLETED";
            String actorId = "admin-001";
            String actorName = "관리자";
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            ClaimHistory result =
                    sut.createStatusChange(
                            claimType, claimId, fromStatus, toStatus, actorId, actorName);

            // then
            assertThat(result.historyType()).isEqualTo(ClaimHistoryType.STATUS_CHANGE);
            assertThat(result.title()).isEqualTo("완료");
            assertThat(result.message()).isEqualTo("COLLECTING → COMPLETED");
        }
    }

    @Nested
    @DisplayName("createStatusChangeBySystem() - 시스템 상태 변경 이력 생성")
    class CreateStatusChangeBySystemTest {

        @Test
        @DisplayName("시스템에 의한 상태 변경 이력을 생성하고 SYSTEM Actor를 설정한다")
        void createStatusChangeBySystem_ValidParams_ReturnsSystemStatusChangeHistory() {
            // given
            ClaimType claimType = ClaimType.REFUND;
            String claimId = "refund-claim-001";
            String fromStatus = "APPROVED";
            String toStatus = "COMPLETED";
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            ClaimHistory result =
                    sut.createStatusChangeBySystem(claimType, claimId, fromStatus, toStatus);

            // then
            assertThat(result).isNotNull();
            assertThat(result.claimType()).isEqualTo(claimType);
            assertThat(result.historyType()).isEqualTo(ClaimHistoryType.STATUS_CHANGE);
            assertThat(result.title()).isEqualTo("완료");
            assertThat(result.actor().actorType()).isEqualTo(ActorType.SYSTEM);
            assertThat(result.actor().actorId()).isEqualTo("system");
            assertThat(result.actor().actorName()).isEqualTo("시스템");
            assertThat(result.createdAt()).isEqualTo(now);
            then(timeProvider).should().now();
        }

        @Test
        @DisplayName("알 수 없는 상태로 변경 시 '상태 변경' 타이틀이 설정된다")
        void createStatusChangeBySystem_UnknownToStatus_SetsDefaultTitle() {
            // given
            ClaimType claimType = ClaimType.CANCEL;
            String claimId = "cancel-claim-001";
            String fromStatus = "REQUESTED";
            String toStatus = "UNKNOWN_STATUS";
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            ClaimHistory result =
                    sut.createStatusChangeBySystem(claimType, claimId, fromStatus, toStatus);

            // then
            assertThat(result.title()).isEqualTo("상태 변경");
        }
    }
}
