package com.ryuqq.marketplace.adapter.in.rest.selleradmin.mapper;

import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ApplySellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkApproveSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkRejectSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ChangeSellerAdminPasswordApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.BulkApproveSellerAdminApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.BulkApproveSellerAdminApiResponse.ItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApplySellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkRejectSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ChangeSellerAdminPasswordCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.RejectSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ResetSellerAdminPasswordCommand;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerAdminApplicationCommandApiMapper - API Request → Application Command 변환.
 *
 * <p>API-MAP-001: Mapper는 @Component로 정의.
 *
 * <p>API-MAP-002: Request → Command 변환만 담당.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminApplicationCommandApiMapper {

    /**
     * 가입 신청 API Request를 Command로 변환합니다.
     *
     * @param request API Request (sellerId 포함)
     * @return Application Command
     */
    public ApplySellerAdminCommand toCommand(ApplySellerAdminApiRequest request) {
        return new ApplySellerAdminCommand(
                request.sellerId(),
                request.loginId(),
                request.name(),
                request.phoneNumber(),
                request.password());
    }

    /**
     * 승인 API Request를 Command로 변환합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID (UUIDv7)
     * @return Application Command
     */
    public ApproveSellerAdminCommand toApproveCommand(String sellerAdminId) {
        return new ApproveSellerAdminCommand(sellerAdminId);
    }

    /**
     * 거절 API Request를 Command로 변환합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID (UUIDv7)
     * @return Application Command
     */
    public RejectSellerAdminCommand toRejectCommand(String sellerAdminId) {
        return new RejectSellerAdminCommand(sellerAdminId);
    }

    /**
     * 일괄 승인 API Request를 Command로 변환합니다.
     *
     * @param request API Request
     * @return Application Command
     */
    public BulkApproveSellerAdminCommand toBulkApproveCommand(
            BulkApproveSellerAdminApiRequest request) {
        return new BulkApproveSellerAdminCommand(request.sellerAdminIds());
    }

    /**
     * 일괄 거절 API Request를 Command로 변환합니다.
     *
     * @param request API Request
     * @return Application Command
     */
    public BulkRejectSellerAdminCommand toBulkRejectCommand(
            BulkRejectSellerAdminApiRequest request) {
        return new BulkRejectSellerAdminCommand(request.sellerAdminIds());
    }

    /**
     * 비밀번호 초기화 요청을 Command로 변환합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID (UUIDv7)
     * @return Application Command
     */
    public ResetSellerAdminPasswordCommand toResetPasswordCommand(String sellerAdminId) {
        return new ResetSellerAdminPasswordCommand(sellerAdminId);
    }

    /**
     * 비밀번호 변경 요청을 Command로 변환합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID (UUIDv7)
     * @param request API Request (newPassword)
     * @return Application Command
     */
    public ChangeSellerAdminPasswordCommand toChangePasswordCommand(
            String sellerAdminId, ChangeSellerAdminPasswordApiRequest request) {
        return new ChangeSellerAdminPasswordCommand(sellerAdminId, request.newPassword());
    }

    /**
     * 일괄 승인 결과를 API Response로 변환합니다.
     *
     * @param result 일괄 처리 결과
     * @return API Response
     */
    public BulkApproveSellerAdminApiResponse toResponse(BatchProcessingResult<String> result) {
        List<ItemResult> items =
                result.results().stream()
                        .map(
                                r ->
                                        new ItemResult(
                                                r.id(),
                                                r.success(),
                                                r.errorCode(),
                                                r.errorMessage()))
                        .toList();
        return new BulkApproveSellerAdminApiResponse(
                result.totalCount(), result.successCount(), result.failureCount(), items);
    }
}
