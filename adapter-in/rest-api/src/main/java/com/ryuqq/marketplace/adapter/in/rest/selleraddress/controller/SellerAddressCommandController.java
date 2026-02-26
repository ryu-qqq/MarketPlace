package com.ryuqq.marketplace.adapter.in.rest.selleraddress.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.SellerAddressAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.command.RegisterSellerAddressApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.command.UpdateSellerAddressApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response.RegisterSellerAddressApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.mapper.SellerAddressCommandApiMapper;
import com.ryuqq.marketplace.application.selleraddress.dto.command.DeleteSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.RegisterSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.UpdateSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.port.in.command.DeleteSellerAddressUseCase;
import com.ryuqq.marketplace.application.selleraddress.port.in.command.RegisterSellerAddressUseCase;
import com.ryuqq.marketplace.application.selleraddress.port.in.command.UpdateSellerAddressUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SellerAddressCommandController - 셀러 주소 생성/수정/삭제 API.
 *
 * <p>셀러 주소 등록, 수정(기본 주소 전환 포함), 삭제(소프트) 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: Controller는 @RestController로 정의.
 *
 * <p>API-CTR-002: DELETE 메서드 금지 (소프트 삭제는 PATCH).
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-004: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>API-CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>API-CTR-007: Controller에 비즈니스 로직 포함 금지.
 *
 * <p>API-CTR-009: @Valid 어노테이션 필수.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "셀러 주소 관리", description = "셀러 주소 생성/수정/삭제 API")
@RestController
@RequestMapping(SellerAddressAdminEndpoints.SELLER_ADDRESSES)
public class SellerAddressCommandController {

    private final RegisterSellerAddressUseCase registerUseCase;
    private final UpdateSellerAddressUseCase updateUseCase;
    private final DeleteSellerAddressUseCase deleteUseCase;
    private final SellerAddressCommandApiMapper mapper;

    /**
     * SellerAddressCommandController 생성자.
     *
     * @param registerUseCase 주소 등록 UseCase
     * @param updateUseCase 주소 수정 UseCase (기본 주소 전환 포함)
     * @param deleteUseCase 주소 삭제 UseCase
     * @param mapper Command API 매퍼
     */
    public SellerAddressCommandController(
            RegisterSellerAddressUseCase registerUseCase,
            UpdateSellerAddressUseCase updateUseCase,
            DeleteSellerAddressUseCase deleteUseCase,
            SellerAddressCommandApiMapper mapper) {
        this.registerUseCase = registerUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.mapper = mapper;
    }

    /**
     * 셀러 주소 등록 API.
     *
     * <p>새로운 셀러 주소를 등록합니다.
     *
     * @param sellerId 셀러 ID
     * @param request 등록 요청 DTO
     * @return 생성된 주소 ID
     */
    @Operation(summary = "셀러 주소 등록", description = "새로운 셀러 주소를 등록합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "201",
                description = "등록 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'seller-address:write')")
    @RequirePermission(value = "seller-address:write", description = "셀러 주소 등록")
    @PostMapping(SellerAddressAdminEndpoints.SELLER)
    public ResponseEntity<ApiResponse<RegisterSellerAddressApiResponse>> register(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(SellerAddressAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Valid @RequestBody RegisterSellerAddressApiRequest request) {

        RegisterSellerAddressCommand command = mapper.toCommand(sellerId, request);
        Long createdId = registerUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(new RegisterSellerAddressApiResponse(createdId)));
    }

    /**
     * 셀러 주소 수정 API.
     *
     * <p>기존 셀러 주소의 정보를 수정합니다.
     *
     * @param sellerId 셀러 ID
     * @param addressId 주소 ID
     * @param request 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "셀러 주소 수정", description = "셀러 주소의 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "주소를 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'seller-address:write')")
    @RequirePermission(value = "seller-address:write", description = "셀러 주소 수정")
    @PutMapping(SellerAddressAdminEndpoints.SELLER + SellerAddressAdminEndpoints.ID)
    public ResponseEntity<Void> update(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(SellerAddressAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Parameter(description = "주소 ID", required = true)
                    @PathVariable(SellerAddressAdminEndpoints.PATH_ADDRESS_ID)
                    Long addressId,
            @Valid @RequestBody UpdateSellerAddressApiRequest request) {

        UpdateSellerAddressCommand command = mapper.toCommand(addressId, request);
        updateUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 셀러 주소 삭제(소프트) API.
     *
     * <p>주소를 소프트 삭제 처리합니다. 기본 주소는 삭제할 수 없습니다.
     *
     * <p>API-CTR-002: DELETE 메서드 금지 → PATCH로 소프트 삭제.
     *
     * @param sellerId 셀러 ID
     * @param addressId 주소 ID
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "셀러 주소 삭제", description = "주소를 소프트 삭제 처리합니다. 기본 주소는 삭제할 수 없습니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "기본 주소는 삭제할 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "주소를 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'seller-address:write')")
    @RequirePermission(value = "seller-address:write", description = "셀러 주소 삭제")
    @PatchMapping(
            SellerAddressAdminEndpoints.SELLER
                    + SellerAddressAdminEndpoints.ID
                    + SellerAddressAdminEndpoints.STATUS)
    public ResponseEntity<Void> delete(
            @Parameter(description = "셀러 ID", required = true)
                    @PathVariable(SellerAddressAdminEndpoints.PATH_SELLER_ID)
                    Long sellerId,
            @Parameter(description = "주소 ID", required = true)
                    @PathVariable(SellerAddressAdminEndpoints.PATH_ADDRESS_ID)
                    Long addressId) {

        DeleteSellerAddressCommand command = mapper.toDeleteCommand(addressId);
        deleteUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
