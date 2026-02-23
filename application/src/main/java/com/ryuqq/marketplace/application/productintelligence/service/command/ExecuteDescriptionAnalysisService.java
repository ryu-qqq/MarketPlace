package com.ryuqq.marketplace.application.productintelligence.service.command;

import com.ryuqq.marketplace.application.productintelligence.dto.command.ExecuteDescriptionAnalysisCommand;
import com.ryuqq.marketplace.application.productintelligence.internal.DescriptionAnalysisProcessor;
import com.ryuqq.marketplace.application.productintelligence.port.in.command.ExecuteDescriptionAnalysisUseCase;
import com.ryuqq.marketplace.application.productintelligence.validator.ProductProfileAnalysisValidator;
import com.ryuqq.marketplace.domain.productintelligence.vo.AnalysisType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Description 분석 실행 서비스.
 *
 * <p>SQS intelligence-description-analysis 큐에서 수신한 메시지를 처리합니다. 프로파일 로드 + 멱등성 검증은 {@link
 * ProductProfileAnalysisValidator}에 위임하고, 실제 분석 로직은 {@link DescriptionAnalysisProcessor}에 위임합니다.
 */
@Service
@ConditionalOnProperty(name = "intelligence.pipeline.enabled", havingValue = "true")
public class ExecuteDescriptionAnalysisService implements ExecuteDescriptionAnalysisUseCase {

    private final ProductProfileAnalysisValidator validator;
    private final DescriptionAnalysisProcessor processor;

    public ExecuteDescriptionAnalysisService(
            ProductProfileAnalysisValidator validator, DescriptionAnalysisProcessor processor) {
        this.validator = validator;
        this.processor = processor;
    }

    @Override
    public void execute(ExecuteDescriptionAnalysisCommand command) {
        validator
                .validateAndLoad(command.profileId(), AnalysisType.DESCRIPTION)
                .ifPresent(profile -> processor.process(profile, command.productGroupId()));
    }
}
