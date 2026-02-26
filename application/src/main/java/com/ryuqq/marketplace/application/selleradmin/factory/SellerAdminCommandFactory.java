package com.ryuqq.marketplace.application.selleradmin.factory;

import com.ryuqq.marketplace.application.common.dto.command.BulkStatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.selleradmin.dto.bundle.SellerAdminApprovalBundle;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApplySellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkRejectSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.RejectSellerAdminCommand;
import com.ryuqq.marketplace.domain.common.vo.PhoneNumber;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.vo.AdminName;
import com.ryuqq.marketplace.domain.selleradmin.vo.LoginId;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerAdmin Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * <p>IdGeneratorPort를 통해 UUIDv7 기반의 ID를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminCommandFactory {

    private final TimeProvider timeProvider;
    private final IdGeneratorPort idGenerator;

    public SellerAdminCommandFactory(TimeProvider timeProvider, IdGeneratorPort idGenerator) {
        this.timeProvider = timeProvider;
        this.idGenerator = idGenerator;
    }

    /**
     * 가입 신청 Command로부터 SellerAdmin 도메인 객체 생성.
     *
     * <p>승인 대기(PENDING_APPROVAL) 상태로 생성됩니다.
     *
     * @param command 가입 신청 Command
     * @return SellerAdmin 도메인 객체
     */
    public SellerAdmin createForApplication(ApplySellerAdminCommand command) {
        String id = idGenerator.generate();
        Instant now = timeProvider.now();

        return SellerAdmin.forApplication(
                SellerAdminId.forNew(id),
                SellerId.of(command.sellerId()),
                LoginId.of(command.loginId()),
                AdminName.of(command.name()),
                PhoneNumber.of(command.phoneNumber()),
                now);
    }

    /**
     * 거절 Command로부터 StatusChangeContext 생성.
     *
     * @param command 거절 Command
     * @return StatusChangeContext (셀러 ID, 셀러 관리자 ID, 변경 시간)
     */
    public StatusChangeContext<SellerAdminId> createRejectContext(
            RejectSellerAdminCommand command) {
        return new StatusChangeContext<>(
                SellerAdminId.of(command.sellerAdminId()), timeProvider.now());
    }

    /**
     * 일괄 거절 Command로부터 BulkStatusChangeContext 생성.
     *
     * @param command 일괄 거절 Command
     * @return BulkStatusChangeContext (셀러 관리자 ID 목록, 변경 시간)
     */
    public BulkStatusChangeContext<SellerAdminId> createBulkRejectContext(
            BulkRejectSellerAdminCommand command) {
        List<SellerAdminId> ids = command.sellerAdminIds().stream().map(SellerAdminId::of).toList();
        return new BulkStatusChangeContext<>(ids, timeProvider.now());
    }

    /**
     * 셀러 관리자 승인 Bundle을 생성합니다.
     *
     * <p>SellerAdmin과 인증 서버 연동용 AuthOutbox를 묶어 반환합니다. 이벤트는 SellerAdmin.approve() 내부에서 생성됩니다.
     *
     * @param sellerAdmin 승인할 셀러 관리자
     * @param organizationId 인증 서버 조직 ID (Seller에서 조회)
     * @return SellerAdminApprovalBundle
     */
    public SellerAdminApprovalBundle createApprovalBundle(
            SellerAdmin sellerAdmin, String organizationId) {
        Instant now = timeProvider.now();

        SellerAdminAuthOutbox authOutbox =
                SellerAdminAuthOutbox.forNew(
                        sellerAdmin.id(), buildAuthOutboxPayload(sellerAdmin, organizationId), now);

        return new SellerAdminApprovalBundle(sellerAdmin, authOutbox, now);
    }

    /**
     * 인증 서버 Outbox payload 생성.
     *
     * <p>인증 서버에 사용자 생성 시 필요한 정보를 JSON으로 변환합니다.
     *
     * @param sellerAdmin 셀러 관리자
     * @param organizationId 인증 서버 조직 ID
     * @return JSON payload
     */
    private String buildAuthOutboxPayload(SellerAdmin sellerAdmin, String organizationId) {
        return String.format(
                "{\"sellerAdminId\":\"%s\",\"sellerId\":%d,"
                        + "\"organizationId\":\"%s\",\"loginId\":\"%s\","
                        + "\"name\":\"%s\",\"phoneNumber\":\"%s\"}",
                sellerAdmin.idValue(),
                sellerAdmin.sellerIdValue(),
                organizationId,
                sellerAdmin.loginIdValue(),
                sellerAdmin.nameValue(),
                sellerAdmin.phoneNumberValue());
    }
}
