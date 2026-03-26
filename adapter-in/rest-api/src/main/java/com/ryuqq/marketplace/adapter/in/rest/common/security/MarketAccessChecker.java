package com.ryuqq.marketplace.adapter.in.rest.common.security;

import com.ryuqq.authhub.sdk.access.BaseAccessChecker;
import com.ryuqq.authhub.sdk.context.UserContextHolder;
import com.ryuqq.marketplace.application.seller.port.in.query.ResolveSellerIdByOrganizationUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.in.query.ResolveSellerIdBySellerAdminIdUseCase;
import com.ryuqq.marketplace.domain.adminmenu.vo.AdminRole;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * MarketPlace 접근 권한 검사기.
 *
 * <p>AuthHub SDK의 BaseAccessChecker를 상속하여 MarketPlace 도메인별 권한 검사 메서드를 제공합니다.
 *
 * <p>Spring Security {@code @PreAuthorize}에서 SpEL로 사용:
 *
 * <pre>{@code
 * @PreAuthorize("@access.hasPermission('brand:read')")
 * @PreAuthorize("@access.authenticated()")
 * @PreAuthorize("@access.isSellerOwnerOr(#sellerId, 'seller:write')")
 * @PreAuthorize("@access.isSellerAdminOwnerOrSuperAdmin(#sellerAdminId)")
 * @PreAuthorize("@access.isSellerAdminBulkOwnerOrSuperAdmin(#request.sellerAdminIds())")
 * }</pre>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
@Component("access")
public class MarketAccessChecker extends BaseAccessChecker {

    private final ResolveSellerIdByOrganizationUseCase resolveSellerIdUseCase;
    private final ResolveSellerIdBySellerAdminIdUseCase resolveSellerIdBySellerAdminIdUseCase;

    public MarketAccessChecker(
            ResolveSellerIdByOrganizationUseCase resolveSellerIdUseCase,
            ResolveSellerIdBySellerAdminIdUseCase resolveSellerIdBySellerAdminIdUseCase) {
        this.resolveSellerIdUseCase = resolveSellerIdUseCase;
        this.resolveSellerIdBySellerAdminIdUseCase = resolveSellerIdBySellerAdminIdUseCase;
    }

    /**
     * 현재 인증된 사용자의 액터 정보를 반환합니다.
     *
     * <p>셀러 ID, 사용자 이름을 한 번에 조회합니다. SUPER_ADMIN은 sellerIdOrNull이 null, actorId가 0입니다.
     *
     * @return 현재 사용자의 액터 정보
     */
    public ActorInfo resolveActorInfo() {
        Long sellerIdOrNull = resolveSellerIdOrNull();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "SYSTEM";
        return new ActorInfo(sellerIdOrNull, username);
    }

    /** 현재 인증된 사용자의 액터 정보. */
    public record ActorInfo(Long sellerIdOrNull, String username) {

        /** sellerId를 primitive로 변환. SUPER_ADMIN(null)이면 0L. */
        public long actorId() {
            return sellerIdOrNull != null ? sellerIdOrNull : 0L;
        }
    }

    /**
     * 셀러 리소스 소유자 검증.
     *
     * <p>현재 사용자의 organizationId로 sellerId를 조회하여 URL의 sellerId와 비교합니다. SUPER_ADMIN은 자동 통과하고, 본인 소유가
     * 아닌 경우 지정된 권한 보유 여부로 fallback합니다.
     *
     * @param sellerId URL의 셀러 ID
     * @param permission fallback 권한
     * @return 소유자이거나 권한 보유 시 true
     */
    public boolean isSellerOwnerOr(long sellerId, String permission) {
        if (superAdmin()) {
            return true;
        }

        String organizationId = getCurrentOrganizationId();
        if (organizationId == null || organizationId.isBlank()) {
            return hasPermission(permission);
        }

        Optional<Long> resolvedSellerId = resolveSellerIdUseCase.execute(organizationId);
        if (resolvedSellerId.isPresent() && resolvedSellerId.get() == sellerId) {
            return true;
        }

        return hasPermission(permission);
    }

    /**
     * 현재 인증된 사용자의 sellerId를 반환합니다.
     *
     * <p>organizationId로 sellerId를 조회하며, 조회 실패 시 AccessDeniedException을 발생시킵니다.
     *
     * @return 현재 사용자의 sellerId
     * @throws AccessDeniedException 셀러 정보를 찾을 수 없는 경우
     */
    public long resolveCurrentSellerId() {
        String organizationId = getCurrentOrganizationId();
        return resolveSellerIdUseCase
                .execute(organizationId)
                .orElseThrow(() -> new AccessDeniedException("현재 사용자의 셀러 정보를 찾을 수 없습니다"));
    }

    /**
     * 등록 요청에서 sellerId를 해석합니다.
     *
     * <p>SUPER_ADMIN은 요청에 포함된 sellerId를 사용하며, 누락 시 예외를 발생시킵니다. 일반 사용자는 인증 컨텍스트에서 셀러 ID를 해석합니다.
     *
     * @param requestedSellerId 요청에 포함된 sellerId (nullable)
     * @return 해석된 sellerId
     * @throws IllegalArgumentException SUPER_ADMIN이 sellerId를 누락한 경우
     * @throws AccessDeniedException 셀러 정보를 찾을 수 없는 경우
     */
    public long resolveSellerIdForRegistration(Long requestedSellerId) {
        if (superAdmin()) {
            if (requestedSellerId == null) {
                throw new IllegalArgumentException("SUPER_ADMIN은 sellerId를 명시해야 합니다");
            }
            return requestedSellerId;
        }
        return resolveCurrentSellerId();
    }

    /**
     * SUPER_ADMIN이면 null, 일반 사용자이면 sellerId를 반환합니다.
     *
     * <p>배치 상태 변경 등 소유권 검증이 선택적인 엔드포인트에서 사용합니다. null이면 서비스 레이어에서 소유권 검증을 건너뜁니다.
     *
     * @return 셀러 ID (SUPER_ADMIN이면 null)
     * @throws AccessDeniedException 셀러 정보를 찾을 수 없는 경우
     */
    public Long resolveSellerIdOrNull() {
        if (superAdmin()) {
            return null;
        }
        return resolveCurrentSellerId();
    }

    /**
     * 셀러 관리자 소속 검증 (단건).
     *
     * <p>SUPER_ADMIN이거나, 대상 sellerAdminId의 소속 셀러가 현재 사용자의 셀러와 같으면 통과합니다.
     *
     * @param sellerAdminId 대상 셀러 관리자 ID
     * @return SUPER_ADMIN이거나 같은 셀러 소속이면 true
     */
    public boolean isSellerAdminOwnerOrSuperAdmin(String sellerAdminId) {
        if (superAdmin()) {
            return true;
        }
        return isSameSellerAsAdmin(sellerAdminId);
    }

    /**
     * 셀러 관리자 소속 검증 (일괄).
     *
     * <p>SUPER_ADMIN이거나, 대상 sellerAdminId 목록이 모두 현재 사용자와 같은 셀러 소속이면 통과합니다.
     *
     * @param sellerAdminIds 대상 셀러 관리자 ID 목록
     * @return SUPER_ADMIN이거나 전부 같은 셀러 소속이면 true
     */
    public boolean isSellerAdminBulkOwnerOrSuperAdmin(List<String> sellerAdminIds) {
        if (superAdmin()) {
            return true;
        }

        Optional<Long> currentSellerId = resolveCurrentSellerIdOptional();
        if (currentSellerId.isEmpty()) {
            return false;
        }

        return resolveSellerIdBySellerAdminIdUseCase
                .resolveIfAllSameSeller(sellerAdminIds)
                .map(targetSellerId -> targetSellerId.equals(currentSellerId.get()))
                .orElse(false);
    }

    private boolean isSameSellerAsAdmin(String sellerAdminId) {
        Optional<Long> currentSellerId = resolveCurrentSellerIdOptional();
        return currentSellerId
                .map(
                        aLong ->
                                resolveSellerIdBySellerAdminIdUseCase
                                        .execute(sellerAdminId)
                                        .map(targetSellerId -> targetSellerId.equals(aLong))
                                        .orElse(false))
                .orElse(false);
    }

    private Optional<Long> resolveCurrentSellerIdOptional() {
        String organizationId = getCurrentOrganizationId();
        if (organizationId == null || organizationId.isBlank()) {
            return Optional.empty();
        }
        return resolveSellerIdUseCase.execute(organizationId);
    }

    /**
     * 조회 필터용 셀러 ID 목록 해석.
     *
     * <p>SUPER_ADMIN은 요청된 sellerIds를 그대로 사용합니다 (null이면 빈 리스트). 일반 사용자는 자신의 셀러 ID만 포함된 단건 리스트를
     * 반환합니다.
     *
     * @param requestedSellerIds 요청에 포함된 셀러 ID 목록 (nullable)
     * @return 유효한 셀러 ID 목록
     * @throws AccessDeniedException 셀러 정보를 찾을 수 없는 경우
     */
    public List<Long> resolveEffectiveSellerIds(List<Long> requestedSellerIds) {
        if (superAdmin()) {
            return requestedSellerIds != null ? requestedSellerIds : Collections.emptyList();
        }
        return List.of(resolveCurrentSellerId());
    }

    /**
     * 리소스 소유권 검증.
     *
     * <p>SUPER_ADMIN은 자동 통과합니다. 일반 사용자는 현재 셀러 ID와 리소스의 셀러 ID가 일치해야 합니다.
     *
     * @param resourceSellerId 리소스의 셀러 ID
     * @throws AccessDeniedException 소유권이 없는 경우
     */
    public void verifySellerOwnership(long resourceSellerId) {
        if (superAdmin()) {
            return;
        }
        long currentSellerId = resolveCurrentSellerId();
        if (currentSellerId != resourceSellerId) {
            throw new AccessDeniedException("해당 리소스에 접근 권한이 없습니다");
        }
    }

    /**
     * 셀러 관리 권한 확인.
     *
     * @return 셀러 관리 권한 보유 여부
     */
    public boolean canManageSeller() {
        return hasPermission("seller:write");
    }

    /**
     * 상품 관리 권한 확인.
     *
     * @return 상품 관리 권한 보유 여부
     */
    public boolean canManageProduct() {
        return hasPermission("product:write");
    }

    /**
     * 현재 사용자의 가장 높은 AdminRole을 반환.
     *
     * <p>UserContextHolder에서 역할 목록을 조회하고, AdminRole로 변환하여 가장 높은 레벨을 반환합니다. 매칭되는 역할이 없으면
     * AdminRole.VIEWER를 기본값으로 반환합니다.
     *
     * @return 가장 높은 AdminRole
     */
    public AdminRole resolveHighestRole() {
        Set<String> roles = UserContextHolder.getContext().getRoles();
        AdminRole highest = AdminRole.VIEWER;
        for (String roleName : roles) {
            try {
                String normalized = roleName.startsWith("ROLE_") ? roleName.substring(5) : roleName;
                AdminRole role = AdminRole.fromName(normalized);
                if (role.level() > highest.level()) {
                    highest = role;
                }
            } catch (IllegalArgumentException ignored) {
                // 알 수 없는 역할명은 무시
            }
        }
        return highest;
    }
}
