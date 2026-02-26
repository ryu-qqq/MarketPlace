package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.query.SellerQueryPort;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Seller Read Manager. */
@Component
public class SellerReadManager {

    private final SellerQueryPort queryPort;

    public SellerReadManager(SellerQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Seller getById(SellerId id) {
        return queryPort.findById(id).orElseThrow(() -> new SellerNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<Seller> getByIds(List<SellerId> ids) {
        return queryPort.findByIds(ids);
    }

    @Transactional(readOnly = true)
    public boolean existsById(SellerId id) {
        return queryPort.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsBySellerName(String sellerName) {
        return queryPort.existsBySellerName(sellerName);
    }

    @Transactional(readOnly = true)
    public boolean existsBySellerNameExcluding(String sellerName, SellerId excludeId) {
        return queryPort.existsBySellerNameExcluding(sellerName, excludeId);
    }

    @Transactional(readOnly = true)
    public List<Seller> findByCriteria(SellerSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(SellerSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public Optional<Long> findSellerIdByOrganizationId(String organizationId) {
        return queryPort.findSellerIdByOrganizationId(organizationId);
    }
}
