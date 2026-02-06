package com.ryuqq.marketplace.adapter.in.rest.commoncodetype.mapper;

import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.ChangeActiveStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.RegisterCommonCodeTypeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.UpdateCommonCodeTypeApiRequest;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.ChangeActiveStatusCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.RegisterCommonCodeTypeCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.UpdateCommonCodeTypeCommand;
import org.springframework.stereotype.Component;

/**
 * CommonCodeTypeCommandApiMapper - 공통 코드 타입 Command API 변환 매퍼.
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-002: 양방향 변환 지원.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * <p>CQRS 분리: Command 전용 Mapper (QueryApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeTypeCommandApiMapper {

    /**
     * RegisterCommonCodeTypeApiRequest -> RegisterCommonCodeTypeCommand 변환.
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public RegisterCommonCodeTypeCommand toCommand(RegisterCommonCodeTypeApiRequest request) {
        return new RegisterCommonCodeTypeCommand(
                request.code(), request.name(), request.description(), request.displayOrder());
    }

    /**
     * UpdateCommonCodeTypeApiRequest + PathVariable ID -> UpdateCommonCodeTypeCommand 변환.
     *
     * <p>API-DTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateCommonCodeTypeCommand toCommand(
            Long commonCodeTypeId, UpdateCommonCodeTypeApiRequest request) {
        return new UpdateCommonCodeTypeCommand(
                commonCodeTypeId, request.name(), request.description(), request.displayOrder());
    }

    /**
     * ChangeActiveStatusApiRequest -> ChangeActiveStatusCommand 변환.
     *
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public ChangeActiveStatusCommand toCommand(ChangeActiveStatusApiRequest request) {
        return new ChangeActiveStatusCommand(request.ids(), request.active());
    }
}
