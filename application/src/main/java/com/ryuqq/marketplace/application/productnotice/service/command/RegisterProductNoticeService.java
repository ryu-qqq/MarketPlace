package com.ryuqq.marketplace.application.productnotice.service.command;

import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.factory.ProductNoticeCommandFactory;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.productnotice.port.in.command.RegisterProductNoticeUseCase;
import com.ryuqq.marketplace.application.productnotice.validator.NoticeEntriesValidator;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import org.springframework.stereotype.Service;

/**
 * RegisterProductNoticeService - 상품 그룹 고시정보 등록 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 */
@Service
public class RegisterProductNoticeService implements RegisterProductNoticeUseCase {

    private final ProductNoticeCommandFactory commandFactory;
    private final NoticeEntriesValidator noticeEntriesValidator;
    private final ProductNoticeCommandCoordinator productNoticeCommandCoordinator;

    public RegisterProductNoticeService(
            ProductNoticeCommandFactory commandFactory,
            NoticeEntriesValidator noticeEntriesValidator,
            ProductNoticeCommandCoordinator productNoticeCommandCoordinator) {
        this.commandFactory = commandFactory;
        this.noticeEntriesValidator = noticeEntriesValidator;
        this.productNoticeCommandCoordinator = productNoticeCommandCoordinator;
    }

    @Override
    public Long execute(RegisterProductNoticeCommand command) {
        ProductNotice productNotice = commandFactory.create(command);
        noticeEntriesValidator.validate(productNotice);
        return productNoticeCommandCoordinator.persist(productNotice);
    }
}
