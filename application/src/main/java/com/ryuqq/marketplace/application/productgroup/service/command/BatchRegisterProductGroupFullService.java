package com.ryuqq.marketplace.application.productgroup.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.application.productgroup.port.in.command.BatchRegisterProductGroupFullUseCase;
import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 상품 그룹 배치 등록 Service.
 *
 * <p>APP-SER-001: @Service 어노테이션
 *
 * <p>APP-SER-002: UseCase 구현
 *
 * <p>빈으로 등록된 Virtual Thread Executor를 주입받아 병렬 등록합니다. 각 항목은 Coordinator의 {@code @Transactional}에 의해
 * 독립 트랜잭션으로 처리됩니다.
 *
 * <p><b>@Transactional 미적용</b>: 항목별 독립 트랜잭션을 위해 서비스 레벨 트랜잭션을 사용하지 않습니다.
 */
@Service
public class BatchRegisterProductGroupFullService implements BatchRegisterProductGroupFullUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(BatchRegisterProductGroupFullService.class);

    private final ProductGroupBundleFactory bundleFactory;
    private final FullProductGroupRegistrationCoordinator coordinator;
    private final ExecutorService batchExecutor;

    public BatchRegisterProductGroupFullService(
            ProductGroupBundleFactory bundleFactory,
            FullProductGroupRegistrationCoordinator coordinator,
            @Qualifier("batchExecutor") ExecutorService batchExecutor) {
        this.bundleFactory = bundleFactory;
        this.coordinator = coordinator;
        this.batchExecutor = batchExecutor;
    }

    /**
     * 상품 그룹을 배치로 등록합니다.
     *
     * <p>1. BundleFactory로 등록 번들 생성 (순수 변환, 스레드 안전)
     *
     * <p>2. Virtual Thread Executor로 병렬 등록
     *
     * <p>3. 항목별 try-catch → BatchItemResult 수집
     *
     * @param commands 등록 Command 목록
     * @return 배치 처리 결과
     */
    @Override
    public BatchProcessingResult<Long> execute(List<RegisterProductGroupCommand> commands) {
        List<CompletableFuture<BatchItemResult<Long>>> futures =
                commands.stream()
                        .map(
                                cmd ->
                                        CompletableFuture.supplyAsync(
                                                () -> processOne(cmd), batchExecutor))
                        .toList();

        List<BatchItemResult<Long>> results =
                futures.stream().map(CompletableFuture::join).toList();

        return BatchProcessingResult.from(results);
    }

    private BatchItemResult<Long> processOne(RegisterProductGroupCommand command) {
        try {
            ProductGroupRegistrationBundle bundle = bundleFactory.createProductGroupBundle(command);
            Long productGroupId = coordinator.register(bundle);
            return BatchItemResult.success(productGroupId);
        } catch (DomainException e) {
            log.warn("배치 등록 실패 - errorCode: {}, message: {}", e.code(), e.getMessage());
            return BatchItemResult.failure(null, e.code(), e.getMessage());
        } catch (Exception e) {
            log.error("배치 등록 중 예상치 못한 오류 발생", e);
            return BatchItemResult.failure(null, "INTERNAL_ERROR", e.getMessage());
        }
    }
}
