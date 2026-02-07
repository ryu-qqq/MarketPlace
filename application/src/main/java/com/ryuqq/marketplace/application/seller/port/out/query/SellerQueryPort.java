package com.ryuqq.marketplace.application.seller.port.out.query;

import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.query.SellerSearchCriteria;
import java.util.List;
import java.util.Optional;

/** Seller Query Port. */
public interface SellerQueryPort {

    Optional<Seller> findById(SellerId id);

    List<Seller> findByIds(List<SellerId> ids);

    boolean existsById(SellerId id);

    boolean existsBySellerName(String sellerName);

    boolean existsBySellerNameExcluding(String sellerName, SellerId excludeId);

    List<Seller> findByCriteria(SellerSearchCriteria criteria);

    long countByCriteria(SellerSearchCriteria criteria);

    Optional<Long> findSellerIdByOrganizationId(String organizationId);
}
