package com.ryuqq.marketplace.application.selleradmin.service.query;

import com.ryuqq.marketplace.application.selleradmin.port.in.query.ResolveSellerIdBySellerAdminIdUseCase;
import com.ryuqq.marketplace.application.selleradmin.port.out.query.SellerAdminQueryPort;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * ResolveSellerIdBySellerAdminIdService - sellerAdminId로 sellerId 조회 서비스.
 *
 * <p>셀러 관리자 접근 제어에서 대상 관리자의 소속 셀러를 확인합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Service
public class ResolveSellerIdBySellerAdminIdService
        implements ResolveSellerIdBySellerAdminIdUseCase {

    private final SellerAdminQueryPort sellerAdminQueryPort;

    public ResolveSellerIdBySellerAdminIdService(SellerAdminQueryPort sellerAdminQueryPort) {
        this.sellerAdminQueryPort = sellerAdminQueryPort;
    }

    @Override
    public Optional<Long> execute(String sellerAdminId) {
        if (sellerAdminId == null || sellerAdminId.isBlank()) {
            return Optional.empty();
        }

        return sellerAdminQueryPort
                .findById(SellerAdminId.of(sellerAdminId))
                .map(SellerAdmin::sellerIdValue);
    }

    @Override
    public Optional<Long> resolveIfAllSameSeller(List<String> sellerAdminIds) {
        if (sellerAdminIds == null || sellerAdminIds.isEmpty()) {
            return Optional.empty();
        }

        if (sellerAdminIds.stream().anyMatch(id -> id == null || id.isBlank())) {
            return Optional.empty();
        }

        final List<SellerAdminId> ids;
        try {
            ids = sellerAdminIds.stream().map(SellerAdminId::of).collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }

        List<SellerAdmin> admins = sellerAdminQueryPort.findAllByIds(ids);
        if (admins.size() != sellerAdminIds.size()) {
            return Optional.empty();
        }

        Set<Long> sellerIds =
                admins.stream().map(SellerAdmin::sellerIdValue).collect(Collectors.toSet());

        if (sellerIds.size() != 1) {
            return Optional.empty();
        }

        return Optional.ofNullable(sellerIds.iterator().next());
    }
}
