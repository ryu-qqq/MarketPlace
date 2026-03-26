package com.ryuqq.marketplace.adapter.in.rest.legacy.shadow.filter;

import com.ryuqq.marketplace.adapter.in.rest.legacy.shadow.ShadowContext;
import com.ryuqq.marketplace.application.shadow.dto.ShadowSnapshot;
import com.ryuqq.marketplace.application.shadow.port.out.ShadowSnapshotStore;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Shadow Traffic 트랜잭션 필터.
 *
 * <p>X-Shadow-Mode: verify 헤더가 감지된 POST/PUT/PATCH 요청에 대해:
 *
 * <ol>
 *   <li>프로그래매틱 트랜잭션 시작 (legacyTransactionManager)
 *   <li>컨트롤러 체인 실행 (전체 비즈니스 로직 + DB write)
 *   <li>응답 캡처 → 스냅샷을 Redis에 저장 (correlationId 키)
 *   <li>트랜잭션 롤백 (DB 원상복구)
 *   <li>캡처된 응답을 Gateway에 반환
 * </ol>
 *
 * <p>이후 Gateway가 운영 레거시 서버에 같은 요청을 보내고, SQS를 발행합니다. Python Shadow Lambda가 Redis에서 스냅샷을 조회하고, DMS 복제
 * 후 GET API로 재조회하여 비교합니다.
 *
 * <p>GET 요청은 DB를 변경하지 않으므로 그냥 통과합니다. Lambda가 직접 양쪽을 호출해서 비교합니다.
 */
public class ShadowTransactionFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ShadowTransactionFilter.class);

    private static final String HEADER_SHADOW_MODE = "X-Shadow-Mode";
    private static final String HEADER_CORRELATION_ID = "X-Shadow-Correlation-Id";
    private static final String HEADER_TIMESTAMP = "X-Shadow-Timestamp";
    private static final String SHADOW_MODE_VERIFY = "verify";

    private final PlatformTransactionManager legacyTransactionManager;
    private final ShadowSnapshotStore snapshotStore;

    public ShadowTransactionFilter(
            PlatformTransactionManager legacyTransactionManager,
            ShadowSnapshotStore snapshotStore) {
        this.legacyTransactionManager = legacyTransactionManager;
        this.snapshotStore = snapshotStore;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String shadowMode = request.getHeader(HEADER_SHADOW_MODE);

        if (!SHADOW_MODE_VERIFY.equals(shadowMode)) {
            filterChain.doFilter(request, response);
            return;
        }

        String method = request.getMethod();

        // GET: DB 변경 없으므로 그냥 통과 (Lambda가 직접 비교)
        if (isReadOnly(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // POST/PUT/PATCH: 트랜잭션 내 실행 → 스냅샷 캡처 → 롤백
        String correlationId = request.getHeader(HEADER_CORRELATION_ID);
        String timestamp = request.getHeader(HEADER_TIMESTAMP);
        String path = request.getRequestURI();

        log.info(
                "[Shadow] 쓰기 요청 수신. correlationId={}, method={}, path={}",
                correlationId,
                method,
                path);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
        txDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus tx = legacyTransactionManager.getTransaction(txDef);

        try {
            ShadowContext.activate(correlationId, method, path);
            filterChain.doFilter(request, responseWrapper);
        } catch (Exception e) {
            log.warn("[Shadow] 요청 처리 중 예외 발생. correlationId={}, path={}", correlationId, path, e);
            safeRollback(tx, correlationId);
            ShadowContext.clear();
            throw e;
        }

        // 스냅샷 캡처 → Redis 저장
        saveSnapshot(correlationId, timestamp, method, path, responseWrapper);

        // 트랜잭션 롤백 (DB 원상복구)
        safeRollback(tx, correlationId);
        ShadowContext.clear();

        // 캡처된 응답을 Gateway에 반환
        responseWrapper.copyBodyToResponse();

        log.info(
                "[Shadow] 처리 완료. correlationId={}, method={}, path={}, status={}",
                correlationId,
                method,
                path,
                responseWrapper.getStatus());
    }

    private void saveSnapshot(
            String correlationId,
            String timestamp,
            String method,
            String path,
            ContentCachingResponseWrapper responseWrapper) {

        String responseBody =
                new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        int statusCode = responseWrapper.getStatus();

        ShadowSnapshot snapshot =
                new ShadowSnapshot(
                        correlationId, timestamp, method, path, statusCode, responseBody);

        try {
            snapshotStore.save(snapshot);
            log.info("[Shadow] 스냅샷 저장 완료. correlationId={}, status={}", correlationId, statusCode);
        } catch (Exception e) {
            log.error("[Shadow] 스냅샷 저장 실패. correlationId={}", correlationId, e);
        }
    }

    private void safeRollback(TransactionStatus tx, String correlationId) {
        try {
            if (!tx.isCompleted()) {
                legacyTransactionManager.rollback(tx);
                log.info("[Shadow] 트랜잭션 롤백 완료. correlationId={}", correlationId);
            }
        } catch (Exception e) {
            log.error("[Shadow] 트랜잭션 롤백 실패. correlationId={}", correlationId, e);
        }
    }

    private boolean isReadOnly(String method) {
        return "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method);
    }
}
