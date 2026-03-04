package com.ryuqq.marketplace.adapter.in.rest.selleradmin.controller;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.SellerAdminPublicEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.query.VerifySellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.VerifySellerAdminApiResponse;
import com.ryuqq.marketplace.application.selleradmin.dto.query.VerifySellerAdminQuery;
import com.ryuqq.marketplace.application.selleradmin.dto.response.VerifySellerAdminResult;
import com.ryuqq.marketplace.application.selleradmin.port.in.query.VerifySellerAdminUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SellerAdminPublicQueryController - 셀러 관리자 공개 Query API.
 *
 * <p>인증 없이 접근 가능한 셀러 관리자 조회 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: Controller는 @RestController로 정의.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-004: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>API-CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>API-CTR-007: Controller에 비즈니스 로직 포함 금지.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag(name = "셀러 관리자 (공개)", description = "셀러 관리자 공개 API")
@RestController
@RequestMapping(SellerAdminPublicEndpoints.BASE)
public class SellerAdminPublicQueryController {

    private final VerifySellerAdminUseCase verifyUseCase;

    public SellerAdminPublicQueryController(VerifySellerAdminUseCase verifyUseCase) {
        this.verifyUseCase = verifyUseCase;
    }

    /**
     * 셀러 관리자 본인 확인 API.
     *
     * <p>이름과 핸드폰 번호로 셀러 관리자 존재 여부 및 상태를 확인합니다. 비밀번호 찾기 전 본인 확인 용도.
     *
     * @param request 이름, 핸드폰 번호
     * @return 존재 여부 및 상태
     */
    @Operation(summary = "셀러 관리자 본인 확인", description = "이름과 핸드폰 번호로 셀러 관리자 존재 여부 및 승인 상태를 확인합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "확인 완료")
    })
    @GetMapping(SellerAdminPublicEndpoints.VERIFY)
    public ResponseEntity<ApiResponse<VerifySellerAdminApiResponse>> verify(
            @Valid @ParameterObject VerifySellerAdminApiRequest request) {

        VerifySellerAdminQuery query =
                VerifySellerAdminQuery.of(request.name(), request.phoneNumber());
        VerifySellerAdminResult result = verifyUseCase.execute(query);

        return ResponseEntity.ok(ApiResponse.of(VerifySellerAdminApiResponse.from(result)));
    }
}
