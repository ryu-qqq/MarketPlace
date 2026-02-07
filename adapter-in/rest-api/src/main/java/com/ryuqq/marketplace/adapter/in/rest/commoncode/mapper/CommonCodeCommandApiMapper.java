package com.ryuqq.marketplace.adapter.in.rest.commoncode.mapper;

import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.ChangeActiveStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.RegisterCommonCodeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.UpdateCommonCodeApiRequest;
import com.ryuqq.marketplace.application.commoncode.dto.command.ChangeCommonCodeStatusCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.RegisterCommonCodeCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.UpdateCommonCodeCommand;
import org.springframework.stereotype.Component;

/**
 * CommonCodeCommandApiMapper - 공통 코드 Command Mapper.
 *
 * <p>API Request → Application Command 변환을 담당합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-004: Command Mapper는 toCommand() 메서드 제공.
 *
 * <p>API-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeCommandApiMapper {

    /**
     * RegisterCommonCodeApiRequest → RegisterCommonCodeCommand 변환.
     *
     * @param request API 요청
     * @return RegisterCommonCodeCommand
     */
    public RegisterCommonCodeCommand toCommand(RegisterCommonCodeApiRequest request) {
        return new RegisterCommonCodeCommand(
                request.commonCodeTypeId(),
                request.code(),
                request.displayName(),
                request.displayOrder());
    }

    /**
     * UpdateCommonCodeApiRequest → UpdateCommonCodeCommand 변환.
     *
     * @param id 공통 코드 ID
     * @param request API 요청
     * @return UpdateCommonCodeCommand
     */
    public UpdateCommonCodeCommand toCommand(Long id, UpdateCommonCodeApiRequest request) {
        return new UpdateCommonCodeCommand(id, request.displayName(), request.displayOrder());
    }

    /**
     * ChangeActiveStatusApiRequest → ChangeCommonCodeStatusCommand 변환.
     *
     * @param request API 요청
     * @return ChangeCommonCodeStatusCommand
     */
    public ChangeCommonCodeStatusCommand toCommand(ChangeActiveStatusApiRequest request) {
        return new ChangeCommonCodeStatusCommand(request.ids(), request.active());
    }
}
