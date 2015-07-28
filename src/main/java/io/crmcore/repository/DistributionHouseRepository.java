package io.crmcore.repository;

import io.crmcore.model.DistributionHouse;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DistributionHouseRepository extends PagingAndSortingRepository<DistributionHouse, Long> {
    DistributionHouse findByName(String s);
}
